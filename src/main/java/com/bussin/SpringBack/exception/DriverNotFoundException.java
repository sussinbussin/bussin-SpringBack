package com.bussin.SpringBack.exception;

public class DriverNotFoundException extends RuntimeException{
    public DriverNotFoundException(String message) {
        super(message);
    }

}
