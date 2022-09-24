package com.bussin.SpringBack.exception;

public class RideNotFoundException extends RuntimeException{
    public RideNotFoundException() {

    }

    public RideNotFoundException(String message) {
        super(message);
    }

    public RideNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public RideNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
