package com.suolashare.ufop.operation.read.product;

import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.exception.operation.ReadException;
import com.suolashare.ufop.operation.read.Reader;
import com.suolashare.ufop.operation.read.domain.ReadFile;
import com.suolashare.ufop.util.ReadFileUtils;
import com.suolashare.ufop.util.UFOPUtils;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MinioReader extends Reader {

    private MinioConfig minioConfig;

    public MinioReader(){

    }

    public MinioReader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = UFOPUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    protected InputStream getInputStream(String fileUrl) {
        InputStream inputStream = null;
        try {

            MinioClient minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();

            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileUrl).build());


        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }


}
