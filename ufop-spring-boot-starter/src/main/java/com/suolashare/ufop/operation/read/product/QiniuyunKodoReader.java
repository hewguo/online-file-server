package com.suolashare.ufop.operation.read.product;

import com.qiniu.util.Auth;
import com.suolashare.common.util.HttpsUtils;
import com.suolashare.ufop.config.QiniuyunConfig;
import com.suolashare.ufop.exception.operation.ReadException;
import com.suolashare.ufop.operation.read.Reader;
import com.suolashare.ufop.operation.read.domain.ReadFile;
import com.suolashare.ufop.util.ReadFileUtils;
import com.suolashare.ufop.util.UFOPUtils;

import java.io.IOException;
import java.io.InputStream;

public class QiniuyunKodoReader extends Reader {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoReader(){

    }

    public QiniuyunKodoReader(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = UFOPUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + fileUrl);



        return HttpsUtils.doGet(urlString);
    }


}
