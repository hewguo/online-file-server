package com.suolashare.file.api;

import com.suolashare.file.domain.FileBean;
import com.suolashare.file.dto.file.DownloadFileDTO;
import com.suolashare.file.dto.file.PreviewDTO;
import com.suolashare.file.dto.file.UploadFileDTO;
import com.suolashare.file.vo.file.UploadFileVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFiletransferService {

    UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO);

    void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId);

    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);
    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);
    void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);
    void deleteFile(FileBean fileBean);

    Long selectStorageSizeByUserId(Long userId);
}
