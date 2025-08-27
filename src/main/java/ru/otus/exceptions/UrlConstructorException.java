package ru.otus.exceptions;

public class UrlConstructorException extends RuntimeException {
    public UrlConstructorException(String s) {
        super(s);
    }

    public UrlConstructorException(Exception e) {
        super(e);
    }
}
