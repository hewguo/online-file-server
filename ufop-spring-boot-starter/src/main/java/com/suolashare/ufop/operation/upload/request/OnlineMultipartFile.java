package com.suolashare.ufop.operation.upload.request;

import com.suolashare.ufop.util.UFOPUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class OnlineMultipartFile {

    MultipartFile multipartFile = null;

    public OnlineMultipartFile() {
    }

    public OnlineMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {

        String originalName = getMultipartFile().getOriginalFilename();
        if (!originalName.contains(".")) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public String getExtendName() {
        String originalName = getMultipartFile().getOriginalFilename();
        String extendName = UFOPUtils.getFileExtendName(originalName);
        return extendName;
    }

    public String getFileUrl() {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, getExtendName());
        return fileUrl;
    }

    public String getFileUrl(String identify) {
        String fileUrl = UFOPUtils.getUploadFileUrl(identify, getExtendName());
        return fileUrl;
    }

    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }

    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }

    public long getSize() {
        long size = getMultipartFile().getSize();
        return size;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    private void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
