package com.suolashare.ufop.operation.read.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.suolashare.ufop.config.AliyunConfig;
import com.suolashare.ufop.exception.operation.ReadException;
import com.suolashare.ufop.operation.read.Reader;
import com.suolashare.ufop.operation.read.domain.ReadFile;
import com.suolashare.ufop.util.AliyunUtils;
import com.suolashare.ufop.util.UFOPUtils;
import com.suolashare.ufop.util.ReadFileUtils;

import java.io.IOException;
import java.io.InputStream;

public class AliyunOSSReader extends Reader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSReader(){

    }

    public AliyunOSSReader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = UFOPUtils.getFileExtendName(fileUrl);
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        try {
            return ReadFileUtils.getContentByInputStream(fileType, inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

}
