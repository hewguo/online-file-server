package com.suolashare.ufop.operation.delete.product;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.suolashare.ufop.config.QiniuyunConfig;
import com.suolashare.ufop.exception.operation.DeleteException;
import com.suolashare.ufop.operation.delete.Deleter;
import com.suolashare.ufop.operation.delete.domain.DeleteFile;
import com.suolashare.ufop.util.QiniuyunUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class QiniuyunKodoDeleter extends Deleter {
    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoDeleter(){

    }

    public QiniuyunKodoDeleter(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        Configuration cfg = QiniuyunUtils.getCfg(qiniuyunConfig);
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(qiniuyunConfig.getKodo().getBucketName(), deleteFile.getFileUrl());
        } catch (QiniuException ex) {
            new DeleteException("七牛云删除文件失败", ex);
        }
        deleteCacheFile(deleteFile);

    }
}
