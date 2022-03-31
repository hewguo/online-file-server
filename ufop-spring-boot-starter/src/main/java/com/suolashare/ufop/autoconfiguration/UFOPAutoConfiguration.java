package com.suolashare.ufop.autoconfiguration;

import com.aliyuncs.utils.StringUtils;
import com.github.tobato.fastdfs.FdfsClientConfig;
import com.suolashare.ufop.factory.UFOPFactory;
import com.suolashare.ufop.operation.copy.product.FastDFSCopier;
import com.suolashare.ufop.operation.delete.product.FastDFSDeleter;
import com.suolashare.ufop.operation.download.product.FastDFSDownloader;
import com.suolashare.ufop.operation.preview.product.FastDFSPreviewer;
import com.suolashare.ufop.operation.read.product.FastDFSReader;
import com.suolashare.ufop.operation.upload.product.AliyunOSSUploader;
import com.suolashare.ufop.operation.upload.product.FastDFSUploader;
import com.suolashare.ufop.operation.upload.product.MinioUploader;
import com.suolashare.ufop.operation.upload.product.QiniuyunKodoUploader;
import com.suolashare.ufop.operation.write.product.FastDFSWriter;
import com.suolashare.ufop.util.RedisUtil;
import com.suolashare.ufop.util.UFOPUtils;
import com.suolashare.ufop.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;


@Slf4j
@Configuration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({UFOPProperties.class})
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class UFOPAutoConfiguration {

    @Autowired
    private UFOPProperties ufopProperties;

    @Bean
    public UFOPFactory ufopFactory() {
        UFOPUtils.LOCAL_STORAGE_PATH = ufopProperties.getLocalStoragePath();
        String bucketName = ufopProperties.getBucketName();
        if (StringUtils.isNotEmpty(bucketName)) {
            UFOPUtils.ROOT_PATH = ufopProperties.getBucketName();
        } else {
            UFOPUtils.ROOT_PATH = "upload";
        }
        return new UFOPFactory(ufopProperties);
    }
    @Bean
    public FastDFSCopier fastDFSCreater() {
        return new FastDFSCopier();
    }
    @Bean
    public FastDFSUploader fastDFSUploader() {
        return new FastDFSUploader();
    }
    @Bean
    public FastDFSDownloader fastDFSDownloader() {
        return new FastDFSDownloader();
    }
    @Bean
    public FastDFSDeleter fastDFSDeleter() {
        return new FastDFSDeleter();
    }
    @Bean
    public FastDFSReader fastDFSReader() {
        return new FastDFSReader();
    }
    @Bean
    public FastDFSWriter fastDFSWriter() {
        return new FastDFSWriter();
    }
    @Bean
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer(ufopProperties.getThumbImage());
    }
    @Bean
    public AliyunOSSUploader aliyunOSSUploader() {
        return new AliyunOSSUploader(ufopProperties.getAliyun());
    }
    @Bean
    public MinioUploader minioUploader() {
        return new MinioUploader(ufopProperties.getMinio());
    }
    @Bean
    public QiniuyunKodoUploader qiniuyunKodoUploader() {
        return new QiniuyunKodoUploader(ufopProperties.getQiniuyun());
    }

    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }
    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

}
