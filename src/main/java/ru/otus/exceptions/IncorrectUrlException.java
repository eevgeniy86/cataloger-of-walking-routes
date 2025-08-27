package ru.otus.exceptions;

public class IncorrectUrlException extends RuntimeException {
    public IncorrectUrlException(String s) {
        super(s);
    }

    public IncorrectUrlException(Exception e) {
        super(e);
    }
}
