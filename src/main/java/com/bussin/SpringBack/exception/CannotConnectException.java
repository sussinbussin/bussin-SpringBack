package com.bussin.SpringBack.exception;

public class CannotConnectException extends RuntimeException{
    public CannotConnectException() {

    }

    public CannotConnectException(String message) {
        super(message);
    }

    public CannotConnectException(Throwable throwable) {
        super(throwable);
    }

    public CannotConnectException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
