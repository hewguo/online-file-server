package com.suolashare.ufop.operation.preview.product;

import com.qiniu.util.Auth;
import com.suolashare.common.util.HttpsUtils;
import com.suolashare.ufop.config.QiniuyunConfig;
import com.suolashare.ufop.domain.ThumbImage;
import com.suolashare.ufop.operation.preview.Previewer;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Getter
@Setter
@Slf4j
public class QiniuyunKodoPreviewer extends Previewer {


    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoPreviewer(){

    }

    public QiniuyunKodoPreviewer(QiniuyunConfig qiniuyunConfig, ThumbImage thumbImage) {
        this.qiniuyunConfig = qiniuyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {

        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + previewFile.getFileUrl());

        InputStream inputStream = HttpsUtils.doGet(urlString);

        return inputStream;
    }


}
