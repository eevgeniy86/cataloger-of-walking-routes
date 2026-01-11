package ru.elistratov.converter;

import ru.elistratov.model.domain.Point;

public interface PointToCoordinatesConverter {
    // 55.7558,37.6176 - lat,long
    String convertPointToCoordinates(Point point);
}
