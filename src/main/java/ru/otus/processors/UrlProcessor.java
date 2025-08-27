package ru.otus.processors;

import java.net.URL;
import ru.otus.exceptions.UrlConstructorException;

public interface UrlProcessor<T> {
    URL fromObjectToUrl(T obj) throws UrlConstructorException;

    T fromUrlToObject(URL url);
}
