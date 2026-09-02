package org.example.bs_lingxin.exception;

import lombok.Getter;

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
