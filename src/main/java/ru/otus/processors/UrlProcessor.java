package ru.otus.processors;

import ru.otus.exceptions.UrlConstructorException;

import java.net.URL;

public interface UrlProcessor<T> {
    URL fromObjectToUrl(T obj) throws UrlConstructorException;

    T fromUrlToObject(URL url);
}
