package com.suolashare.ufop.operation.preview.product;

import com.suolashare.ufop.domain.ThumbImage;
import com.suolashare.ufop.operation.preview.Previewer;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import com.suolashare.ufop.util.UFOPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LocalStoragePreviewer extends Previewer {

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        //设置文件路径
        File file = UFOPUtils.getLocalSaveFile(previewFile.getFileUrl());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return inputStream;

    }

}
