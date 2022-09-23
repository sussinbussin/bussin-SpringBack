package com.bussin.SpringBack.exception;

public class PlannedRouteNotFoundException extends RuntimeException {
    public PlannedRouteNotFoundException() {

    }

    public PlannedRouteNotFoundException(String message) {
        super(message);
    }

    public PlannedRouteNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public PlannedRouteNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
