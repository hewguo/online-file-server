package com.suolashare.ufop.operation.write.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.suolashare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.suolashare.ufop.config.AliyunConfig;
import com.suolashare.ufop.operation.write.Writer;
import com.suolashare.ufop.operation.write.domain.WriteFile;
import com.suolashare.ufop.util.AliyunUtils;
import com.suolashare.ufop.util.UFOPUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {

    private AliyunConfig aliyunConfig;

    public AliyunOSSWriter(){

    }

    public AliyunOSSWriter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);

        ossClient.putObject(aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        ossClient.shutdown();
    }



}
