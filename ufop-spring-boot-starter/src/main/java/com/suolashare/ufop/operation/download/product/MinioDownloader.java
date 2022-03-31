package com.suolashare.ufop.operation.download.product;

import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.exception.operation.DownloadException;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MinioDownloader extends Downloader {

    private MinioConfig minioConfig;

    public MinioDownloader(){

    }

    public MinioDownloader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        InputStream inputStream = null;
        try {

            MinioClient minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();

            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(downloadFile.getFileUrl()).build());


        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }

}
