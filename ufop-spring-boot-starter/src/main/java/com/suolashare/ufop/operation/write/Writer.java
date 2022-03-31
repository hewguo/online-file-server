package com.suolashare.ufop.operation.write;

import com.suolashare.ufop.operation.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
