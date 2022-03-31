package com.suolashare.ufop.operation.write.product;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.suolashare.ufop.operation.write.Writer;
import com.suolashare.ufop.operation.write.domain.WriteFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FastDFSWriter extends Writer {
    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        defaultAppendFileStorageClient.modifyFile("group1", writeFile.getFileUrl(), inputStream,
                writeFile.getFileSize(), 0);
    }
}
