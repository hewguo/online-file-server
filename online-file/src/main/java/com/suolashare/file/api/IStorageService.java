package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.StorageBean;

public interface IStorageService extends IService<StorageBean> {
    Long getTotalStorageSize(Long userId);
    boolean checkStorage(Long userId, Long fileSize);
}
