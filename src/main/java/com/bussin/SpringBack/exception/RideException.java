package com.bussin.SpringBack.exception;

public class RideException extends RuntimeException{
    public RideException() {

    }

    public RideException(String message) {
        super(message);
    }
}
