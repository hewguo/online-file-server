package com.suolashare.ufop.operation.copy.product;

import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.operation.copy.Copier;
import com.suolashare.ufop.operation.copy.domain.CopyFile;
import com.suolashare.ufop.util.UFOPUtils;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MinioCopier extends Copier {

    private MinioConfig minioConfig;

    public MinioCopier(){

    }

    public MinioCopier(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());



        MinioClient minioClient = null;
        try {
            minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if(!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileUrl).stream(
                                    inputStream, inputStream.available(), 1024 * 1024 * 5)
//                            .contentType("video/mp4")
                            .build());

        } catch (MinioException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return fileUrl;
    }


}
