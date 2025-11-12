package ru.elistratov.converter;

import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Point;

@Component
public class PointsToOSMCoordinatesConverter {

    // 55.7558,37.6176 - lat,long
    public String convertPointToCoordinates(Point point) {
        return point.latitude() + "," + point.longitude();
    }
}
