package ru.otus.exceptions;

public class WebClientException extends RuntimeException {

    public WebClientException(String s) {
        super(s);
    }

    public WebClientException(Throwable e) {
        super(e);
    }
}
