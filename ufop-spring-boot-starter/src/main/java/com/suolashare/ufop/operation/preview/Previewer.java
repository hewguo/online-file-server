package com.suolashare.ufop.operation.preview;

import com.suolashare.common.operation.ImageOperation;
import com.suolashare.common.operation.VideoOperation;
import com.suolashare.ufop.domain.ThumbImage;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import com.suolashare.ufop.util.CharsetUtils;
import com.suolashare.ufop.util.UFOPUtils;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Data
public abstract class Previewer {

    public ThumbImage thumbImage;

    protected abstract InputStream getInputStream(PreviewFile previewFile);

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = UFOPUtils.isVideoFile(UFOPUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + UFOPUtils.getFileExtendName(fileUrl), ".jpg");
        }


        File saveFile = UFOPUtils.getCacheFile(thumbnailImgUrl);

        if (saveFile.exists()) {
            FileInputStream fis = null;
            OutputStream outputStream = null;
            try {
                fis = new FileInputStream(saveFile);
                outputStream = httpServletResponse.getOutputStream();
                IOUtils.copy(fis, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(outputStream);
            }

        } else {
            InputStream inputstream = null;
            OutputStream outputStream = null;
            InputStream in = null;
            try {
                inputstream = getInputStream(previewFile);
                if (inputstream != null) {
                    outputStream = httpServletResponse.getOutputStream();
                    int thumbImageWidth = thumbImage.getWidth();
                    int thumbImageHeight = thumbImage.getHeight();
                    int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                    int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;

                    if (isVideo) {
                        in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                    } else {
                        in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                    }
                    IOUtils.copy(in, outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputStream);
                if (previewFile.getOssClient() != null) {
                    previewFile.getOssClient().shutdown();
                }
            }


        }
    }

    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = null;

        OutputStream outputStream = null;

        try {
            inputStream = getInputStream(previewFile);
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertTxtCharsetToUTF8(bytes, UFOPUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            if (previewFile.getOssClient() != null) {
                previewFile.getOssClient().shutdown();
            }
        }
    }


}
