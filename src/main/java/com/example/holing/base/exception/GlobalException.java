package com.example.holing.base.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    ExceptionCode exceptionCode;

    public GlobalException(ExceptionCode exceptionCode) {
        super(exceptionCode.getCause());
        this.exceptionCode = exceptionCode;
    }
}
