package com.suolashare.ufop.operation.preview.product;

import com.suolashare.common.operation.ImageOperation;
import com.suolashare.common.operation.VideoOperation;
import com.suolashare.ufop.config.MinioConfig;
import com.suolashare.ufop.domain.ThumbImage;
import com.suolashare.ufop.operation.preview.Previewer;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import com.suolashare.ufop.util.CharsetUtils;
import com.suolashare.ufop.util.UFOPUtils;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@Slf4j
public class MinioPreviewer extends Previewer {
    private MinioConfig minioConfig;

    public MinioPreviewer(){

    }

    public MinioPreviewer(MinioConfig minioConfig, ThumbImage thumbImage) {
        setMinioConfig(minioConfig);
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        InputStream inputStream = null;
        try {

            MinioClient minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();

            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(previewFile.getFileUrl()).build());


        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }


}
