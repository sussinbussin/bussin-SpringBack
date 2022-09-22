package com.bussin.SpringBack.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){

    }

    public UserNotFoundException(String message){
        super(message);
    }

    public UserNotFoundException(Throwable throwable){
        super(throwable);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
