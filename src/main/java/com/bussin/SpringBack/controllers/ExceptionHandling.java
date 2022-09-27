package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.PlannedRouteNotFoundException;
import com.bussin.SpringBack.exception.RideNotFoundException;
import com.bussin.SpringBack.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandling {
    /**
     * Handles DataIntegrityViolationException.
     *
     * @param e DataIntegrityViolationException
     * @return Response entity with HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     *
     * @param e MethodArgumentTypeMismatchException
     * @return Response entity with HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param e MethodArgumentNotValidException
     * @return Response entity with HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param e ConstraintViolationException
     * @return Response entity with HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleConstraintViolationException(
            final ConstraintViolationException e) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserNotFoundException.
     *
     * @param e UserNotFoundException
     * @return Response entity with HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlannedRouteNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            final PlannedRouteNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles RideNotFoundException.
     *
     * @param e RideNotFoundException
     * @return Response entity with HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<String> handleRideNotFoundException(
            final RideNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions uncaught by earlier handlers.
     *
     * @param e Generic exception
     * @return Response entity with HTTP code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(final Exception e) {
        System.out.println(e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
