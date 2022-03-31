package com.suolashare.ufop.operation.upload.domain;

import com.suolashare.ufop.constant.StorageTypeEnum;
import com.suolashare.ufop.constant.UploadFileStatusEnum;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;
    private BufferedImage bufferedImage;

}
