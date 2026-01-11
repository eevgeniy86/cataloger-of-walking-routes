package ru.elistratov.converter;

import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Point;

@Component
public class PointToOSMCoordinatesConverter implements PointToCoordinatesConverter {

    // 55.7558,37.6176 - lat,long
    @Override
    public String convertPointToCoordinates(Point point) {
        return point.latitude() + "," + point.longitude();
    }
}
