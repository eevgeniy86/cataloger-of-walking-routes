package ru.elistratov.converter;

import java.net.URL;
import ru.elistratov.exception.UrlConstructorException;

public interface UrlConverter<T> {
    URL fromObjectToUrl(T obj) throws UrlConstructorException;

    T fromUrlToObject(URL url);
}
