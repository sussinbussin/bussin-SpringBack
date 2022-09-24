package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandling {
    /**
     * Handles DataIntegrityViolationException.
     *
     * @param e DataIntegrityViolationException
     * @return Response entity with HTTP code 400
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException.
     *
     * @param e HttpMessageNotReadableException
     * @return Response entity with HTTP code 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserNotFoundException.
     *
     * @param e UserNotFoundException
     * @return Response entity with HTTP code 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            final UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DriverNotFoundException.
     *
     * @param e DriverNotFoundException
     * @return Response entity with HTTP code 404
     */
    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            final DriverNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DriverNotFoundException.
     *
     * @param e DriverNotFoundException
     * @return Response entity with HTTP code 404
     */
    @ExceptionHandler(PlannedRouteNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            final PlannedRouteNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions uncaught by earlier handlers.
     *
     * @param e Generic exception
     * @return Response entity with HTTP code 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(final Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
