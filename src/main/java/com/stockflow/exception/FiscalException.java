package com.stockflow.exception;

import org.springframework.http.HttpStatus;

public class FiscalException extends BusinessException {

    public FiscalException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }

    public FiscalException(String message, HttpStatus status) {
        super(message, status);
    }
}
