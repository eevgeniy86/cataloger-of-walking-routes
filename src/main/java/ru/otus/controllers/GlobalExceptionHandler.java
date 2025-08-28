package ru.otus.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.otus.exceptions.IncorrectUrlException;
import ru.otus.exceptions.RouteNotFoundException;
import ru.otus.exceptions.UrlConstructorException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RouteNotFoundException.class)
    public ProblemDetail handleRouteNotFoundException(RouteNotFoundException e, WebRequest request) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IncorrectUrlException.class)
    public ProblemDetail handleIncorrectUrlException(IncorrectUrlException e, WebRequest request) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UrlConstructorException.class)
    public ProblemDetail handleUrlConstructorException(UrlConstructorException e, WebRequest request) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
