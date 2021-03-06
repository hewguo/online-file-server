package com.suolashare.ufop.operation.upload.product;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.suolashare.ufop.config.AliyunConfig;
import com.suolashare.ufop.constant.StorageTypeEnum;
import com.suolashare.ufop.constant.UploadFileStatusEnum;
import com.suolashare.ufop.operation.upload.Uploader;
import com.suolashare.ufop.operation.upload.domain.UploadFile;
import com.suolashare.ufop.operation.upload.domain.UploadFileInfo;
import com.suolashare.ufop.operation.upload.domain.UploadFileResult;
import com.suolashare.ufop.operation.upload.request.OnlineMultipartFile;
import com.suolashare.ufop.util.AliyunUtils;
import com.suolashare.ufop.util.RedisUtil;
import com.suolashare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
public class AliyunOSSUploader extends Uploader {

    @Resource
    RedisUtil redisUtil;

    private AliyunConfig aliyunConfig;

    public AliyunOSSUploader(){

    }

    public AliyunOSSUploader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    protected void doUploadFileChunk(OnlineMultipartFile onlineMultipartFile, UploadFile uploadFile) throws IOException {

        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
            String fileUrl = onlineMultipartFile.getFileUrl();
            if (uploadFileInfo == null) {

                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), fileUrl);
                InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
                String uploadId = upresult.getUploadId();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(aliyunConfig.getOss().getBucketName());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(uploadId);

                redisUtil.set("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest", JSON.toJSONString(uploadFileInfo));

            }

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
            uploadPartRequest.setKey(uploadFileInfo.getKey());
            uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
            uploadPartRequest.setInputStream(onlineMultipartFile.getUploadInputStream());
            uploadPartRequest.setPartSize(onlineMultipartFile.getSize());
            uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
            log.debug(JSON.toJSONString(uploadPartRequest));

            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

            log.debug("???????????????" + JSON.toJSONString(uploadPartResult));

            if (redisUtil.hasKey("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags")) {
                List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            } else {
                List<PartETag> partETags = new ArrayList<PartETag>();
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            }
        } finally {
            ossClient.shutdown();
        }


    }

    @Override
    protected UploadFileResult organizationalResults(OnlineMultipartFile onlineMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        uploadFileResult.setFileUrl(uploadFileInfo.getKey());
        uploadFileResult.setFileName(onlineMultipartFile.getFileName());
        uploadFileResult.setExtendName(onlineMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(onlineMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("??????????????????");
            completeMultipartUpload(uploadFile);
            redisUtil.deleteKey("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            redisUtil.deleteKey("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags");
            redisUtil.deleteKey("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");
            if (UFOPUtils.isImageFile(uploadFileResult.getExtendName())) {

                OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
                OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                        UFOPUtils.getAliyunObjectNameByFileUrl(uploadFileResult.getFileUrl()));
                InputStream is = ossObject.getObjectContent();
                BufferedImage src = null;
                try {
                    src = ImageIO.read(is);
                    uploadFileResult.setBufferedImage(src);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }

            }
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);

        }
        return uploadFileResult;
    }


    /**
     * ?????????????????????????????????????????????????????????
     * @param uploadFile ????????????
     */
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);

        Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        // ???????????????
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();

    }

    /**
     * ????????????
     */
    @Override
    public void cancelUpload(UploadFile uploadFile) {

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("OnlineUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId());
        ossClient.abortMultipartUpload(abortMultipartUploadRequest);
        ossClient.shutdown();
    }


}
