package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.FileBean;

public interface IFileService  extends IService<FileBean> {

    Long getFilePointCount(Long fileId);
    void unzipFile(long userFileId, int unzipMode, String filePath);





}
