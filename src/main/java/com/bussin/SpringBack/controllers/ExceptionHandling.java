package com.bussin.SpringBack.controllers;

import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bussin.SpringBack.exception.*;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${debugMode}")
    private boolean debugMode;

    /**
     * Handles DataIntegrityViolationException.
     *
     * @param e DataIntegrityViolationException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e) {
        String userMessage = "Invalid input, please try again";
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException.
     *
     * @param e HttpMessageNotReadableException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e) {
        String userMessage = "Unable to read input";
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     *
     * @param e MethodArgumentTypeMismatchException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e) {
        String userMessage = "Invalid inputs";
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param e MethodArgumentNotValidException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        String userMessage = "Fields are not in correct format";
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param e ConstraintViolationException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            final ConstraintViolationException e) {
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles RideException.
     * 
     * @param e RideException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RideException.class)
    public ResponseEntity<ApiError> handleRideException(
            final RideException e) {
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidPasswordException.
     *
     * @param e InvalidPasswordException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(
            final InvalidPasswordException e) {
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UsernameExistsException.
     *
     * @param e UsernameExistsException
     * @return Response entity with ApiError message and HTTP code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ApiError> handleUsernameExistsException(
            final UsernameExistsException e) {
        String devMessage = "400 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles JWTVerificationException.
     *
     * @param e JWTVerificationException
     * @return Response entity with ApiError message and HTTP code 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApiError> handleJWTVerificationException(
            final JWTVerificationException e) {
        String userMessage = "Unable to verify user";
        String devMessage = "401 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles WrongUserException.
     *
     * @param e WrongUserException
     * @return Response entity with ApiError message and HTTP code 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(WrongUserException.class)
    public ResponseEntity<ApiError> handleWrongUserException(
            final WrongUserException e) {
        String userMessage = "You are not allowed to view this page";
        String devMessage = "403 caused by WrongUserException. The UUID of " +
                "the requesting user and target user don't match " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage,
                e.getStackTrace()), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles WrongDriverException.
     *
     * @param e WrongDriverException
     * @return Response entity with ApiError message and HTTP code 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(WrongDriverException.class)
    public ResponseEntity<ApiError> handleWrongDriverException(
            final WrongDriverException e) {
        String userMessage = "You are not allowed to view this page";
        String devMessage = "403 caused by WrongDriverException. The UUID of " +
                "the requesting driver and target driver don't match " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage,
                e.getStackTrace()), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles UserNotFoundException.
     *
     * @param e UserNotFoundException
     * @return Response entity with ApiError message and HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(
            final UserNotFoundException e) {
        String devMessage = "404 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DriverNotFoundException.
     *
     * @param e DriverNotFoundException
     * @return Response entity with ApiError and HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ApiError> handleDriverNotFoundException(
            final DriverNotFoundException e) {
        String devMessage = "404 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles PlannedRouteNotFoundException.
     *
     * @param e PlannedRouteNotFoundException
     * @return Response entity with ApiError message and HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlannedRouteNotFoundException.class)
    public ResponseEntity<ApiError> handlePlannedRouteNotFoundException(
            final PlannedRouteNotFoundException e) {
        String devMessage = "404 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles RideNotFoundException.
     *
     * @param e RideNotFoundException
     * @return Response entity with ApiError message and HTTP code 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ApiError> handleRideNotFoundException(
            final RideNotFoundException e) {
        String devMessage = "404 caused by " + e;
        return new ResponseEntity<>(new ApiError(e.getMessage(), devMessage, e.getStackTrace()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions uncaught by earlier handlers.
     *
     * @param e Generic exception
     * @return Response entity with ApiError message and HTTP code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(final Exception e) {
        String userMessage = "Something went wrong";
        String devMessage = "500 caused by " + e;
        return new ResponseEntity<>(new ApiError(userMessage, devMessage, e.getStackTrace()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
