package com.suolashare.ufop.config;

import com.suolashare.ufop.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
