package com.app.wallet.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String msg) {
        super(msg);
    }
}
