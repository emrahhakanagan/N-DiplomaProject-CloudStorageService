package com.agan.cloudstorage.exception;

public class GeneralServiceException extends RuntimeException {
    public GeneralServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralServiceException(String message) {
        super(message);
    }
}
