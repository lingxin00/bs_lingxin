package org.example.bs_lingxin.common.exception;

import lombok.Getter;
/**
 * 自定义异常类
 * */
@Getter
public class BusinessException extends RuntimeException{
    private Integer code;

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code=500;
    }
}
