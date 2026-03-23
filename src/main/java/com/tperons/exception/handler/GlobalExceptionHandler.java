package com.tperons.exception.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tperons.exception.BadRequestException;
import com.tperons.exception.ExceptionResponse;
import com.tperons.exception.FileNotFoundException;
import com.tperons.exception.FileStorageException;
import com.tperons.exception.InvalidJwtAuthenticationException;
import com.tperons.exception.ObjectAlreadyExistsException;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.exception.ResourceNotFoundException;

@RestControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredObjectIsNullException.class)
    public final ResponseEntity<ExceptionResponse> handleRequiredObjectExceptions(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public final ResponseEntity<ExceptionResponse> handleFileStorageException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleFileNotFoundException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationException(Exception e,
            WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public final ResponseEntity<ExceptionResponse> handleUnsupportedOperationException(Exception e,
            WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public final ResponseEntity<ExceptionResponse> handleObjectAlreadyExistsException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                e.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
