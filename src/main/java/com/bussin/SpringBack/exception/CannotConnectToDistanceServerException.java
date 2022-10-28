package com.bussin.SpringBack.exception;

public class CannotConnectToDistanceServerException extends RuntimeException{
    public CannotConnectToDistanceServerException(String message) {
        super(message);
    }
}
