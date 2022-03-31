package com.suolashare.ufop.operation.download.product;

import com.qiniu.util.Auth;
import com.suolashare.common.util.HttpsUtils;
import com.suolashare.ufop.config.QiniuyunConfig;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;

import java.io.InputStream;

public class QiniuyunKodoDownloader extends Downloader {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoDownloader(){

    }

    public QiniuyunKodoDownloader(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + downloadFile.getFileUrl());

        InputStream inputStream = HttpsUtils.doGet(urlString);

        return inputStream;
    }

}
