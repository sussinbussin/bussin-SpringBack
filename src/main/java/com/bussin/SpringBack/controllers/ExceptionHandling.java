package com.bussin.SpringBack.controllers;

import com.bussin.SpringBack.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandling {
    /**
     * Handles UserNotFoundException.
     *
     * @param e UserNotFoundException
     * @return Response entity with Error and HTTP code 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> handleException(final UserNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions uncaught by earlier handlers.
     *
     * @param e Generic exception
     * @return Response entity with Error and HTTP code 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(final Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
