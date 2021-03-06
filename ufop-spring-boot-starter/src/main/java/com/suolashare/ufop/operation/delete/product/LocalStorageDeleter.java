package com.suolashare.ufop.operation.delete.product;

import com.suolashare.ufop.exception.operation.DeleteException;
import com.suolashare.ufop.operation.delete.Deleter;
import com.suolashare.ufop.operation.delete.domain.DeleteFile;
import com.suolashare.ufop.util.UFOPUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = UFOPUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            boolean result = localSaveFile.delete();
            if (!result) {
                new DeleteException("删除本地文件失败");
            }
        }

        deleteCacheFile(deleteFile);
    }
}
