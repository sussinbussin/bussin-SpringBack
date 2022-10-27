package com.bussin.SpringBack.exception;

public class PlannedRouteNotFoundException extends RuntimeException {
    public PlannedRouteNotFoundException(String message) {
        super(message);
    }
}
