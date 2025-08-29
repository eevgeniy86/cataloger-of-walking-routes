package ru.otus.Converters;

import ru.otus.model.domain.Point;

public class PointsToOSMCoordinatesConverter {

    // 55.7558,37.6176 - lat,long
    public static String convertPointToCoordinates(Point point) {
        return point.latitude() + "," + point.longitude();
    }
}
