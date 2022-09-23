package com.bussin.SpringBack.exception;

public class DriverNotFoundException extends RuntimeException{
    public DriverNotFoundException(){

    }

    public DriverNotFoundException(String message){
        super(message);
    }

    public DriverNotFoundException(Throwable throwable){
        super(throwable);
    }

    public DriverNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
