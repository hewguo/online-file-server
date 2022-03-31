package com.suolashare.ufop.autoconfiguration;

import com.suolashare.ufop.config.AliyunConfig;
import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.config.QiniuyunConfig;
import com.suolashare.ufop.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ufop")
public class UFOPProperties {

    private String bucketName;
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
    private ThumbImage thumbImage = new ThumbImage();
    private MinioConfig minio = new MinioConfig();
    private QiniuyunConfig qiniuyun = new QiniuyunConfig();
}
