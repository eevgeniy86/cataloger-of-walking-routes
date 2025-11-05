package ru.otus.Converters;

import org.springframework.stereotype.Component;
import ru.otus.model.domain.Point;

@Component
public class PointsToOSMCoordinatesConverter {

    // 55.7558,37.6176 - lat,long
    public String convertPointToCoordinates(Point point) {
        return point.latitude() + "," + point.longitude();
    }
}
