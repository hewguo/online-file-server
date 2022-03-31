package com.suolashare.ufop.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.suolashare.ufop.config.AliyunConfig;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.util.AliyunUtils;
import com.suolashare.ufop.util.UFOPUtils;

import java.io.InputStream;

public class AliyunOSSDownloader extends Downloader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSDownloader(){

    }

    public AliyunOSSDownloader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        downloadFile.setOssClient(ossClient);
        return inputStream;
    }

}
