package com.suolashare.ufop.operation.delete.product;

import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.exception.operation.DeleteException;
import com.suolashare.ufop.operation.delete.Deleter;
import com.suolashare.ufop.operation.delete.domain.DeleteFile;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MinioDeleter extends Deleter {
    private MinioConfig minioConfig;

    public MinioDeleter(){

    }

    public MinioDeleter(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {

        try {
            MinioClient minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();
            // 从mybucket中删除myobject。
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioConfig.getBucketName()).object(deleteFile.getFileUrl()).build());
            log.info("successfully removed mybucket/myobject");
        } catch (MinioException e) {
            log.error("Error: " + e);
            throw new DeleteException("Minio删除文件失败", e);
        } catch (IOException e) {
            throw new DeleteException("Minio删除文件失败", e);
        } catch (NoSuchAlgorithmException e) {
            throw new DeleteException("Minio删除文件失败", e);
        } catch (InvalidKeyException e) {
            throw new DeleteException("Minio删除文件失败", e);
        }
        deleteCacheFile(deleteFile);

    }
}
