package com.suolashare.ufop.operation.read.product;

import com.suolashare.ufop.exception.operation.ReadException;
import com.suolashare.ufop.operation.read.Reader;
import com.suolashare.ufop.operation.read.domain.ReadFile;
import com.suolashare.ufop.util.UFOPUtils;
import com.suolashare.ufop.util.ReadFileUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            String extendName = UFOPUtils.getFileExtendName(readFile.getFileUrl());
            FileInputStream fileInputStream = new FileInputStream(UFOPUtils.getStaticPath() + readFile.getFileUrl());
            fileContent = ReadFileUtils.getContentByInputStream(extendName, fileInputStream);
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
