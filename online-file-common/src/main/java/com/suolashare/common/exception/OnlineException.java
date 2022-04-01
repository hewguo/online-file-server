package com.suolashare.common.exception;

import com.suolashare.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 */
@Data
public class OnlineException extends RuntimeException {
    private Integer code;

    public OnlineException(String message) {
        super(message);
        this.code = ResultCodeEnum.UNKNOWN_ERROR.getCode();
    }

    public OnlineException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public OnlineException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "OnlineException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}