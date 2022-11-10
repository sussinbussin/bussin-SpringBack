package com.bussin.SpringBack.exception;

public class WrongUserException extends RuntimeException {
    public WrongUserException(String message) {
        super(message);
    }
}
