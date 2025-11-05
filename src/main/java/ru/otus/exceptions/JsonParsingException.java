package ru.otus.exceptions;

public class JsonParsingException extends RuntimeException {
    public JsonParsingException(String s) {
        super(s);
    }

    public JsonParsingException(Exception e) {
        super(e);
    }
}
