package com.bussin.SpringBack.exception;

public class CannotConnectException extends RuntimeException{
    public CannotConnectException(Throwable throwable) {
        super(throwable);
    }
}
