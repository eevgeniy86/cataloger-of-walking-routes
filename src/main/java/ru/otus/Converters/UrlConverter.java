package ru.otus.Converters;

import java.net.URL;
import ru.otus.exceptions.UrlConstructorException;

public interface UrlConverter<T> {
    URL fromObjectToUrl(T obj) throws UrlConstructorException;

    T fromUrlToObject(URL url);
}
