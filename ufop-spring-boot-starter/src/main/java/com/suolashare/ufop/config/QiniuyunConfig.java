package com.suolashare.ufop.config;

import com.suolashare.ufop.domain.QiniuyunKodo;
import lombok.Data;

@Data
public class QiniuyunConfig {
    private QiniuyunKodo kodo = new QiniuyunKodo();
}
