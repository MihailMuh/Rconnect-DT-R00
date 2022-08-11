package com.mihalis.dtr00.utils;

public class DTR00Exception extends RuntimeException {
    public DTR00Exception() {
    }

    public DTR00Exception(String message) {
        super(message);
    }

    public DTR00Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public DTR00Exception(Throwable cause) {
        super(cause);
    }

    public DTR00Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
