package com.bussin.SpringBack.exception;

public class CannotConnectToDistanceServerException extends RuntimeException{
    public CannotConnectToDistanceServerException() {

    }

    public CannotConnectToDistanceServerException(String message) {
        super(message);
    }

    public CannotConnectToDistanceServerException(Throwable throwable) {
        super(throwable);
    }

    public CannotConnectToDistanceServerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
