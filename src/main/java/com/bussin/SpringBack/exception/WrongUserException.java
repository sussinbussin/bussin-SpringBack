package com.bussin.SpringBack.exception;

public class WrongUserException extends RuntimeException{
    public WrongUserException() {

    }

    public WrongUserException(String message) {
        super(message);
    }

    public WrongUserException(Throwable throwable) {
        super(throwable);
    }

    public WrongUserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
