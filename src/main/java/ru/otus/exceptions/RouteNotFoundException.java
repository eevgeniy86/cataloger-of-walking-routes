package ru.otus.exceptions;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String s) {
        super(s);
    }

    public RouteNotFoundException(Exception e) {
        super(e);
    }
}
