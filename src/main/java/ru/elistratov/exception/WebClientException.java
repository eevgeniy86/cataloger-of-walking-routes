package ru.elistratov.exception;

public class WebClientException extends RuntimeException {

    public WebClientException(String s) {
        super(s);
    }

    public WebClientException(Throwable e) {
        super(e);
    }
}
