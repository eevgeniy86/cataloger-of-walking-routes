package ru.elistratov.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Point;

@Component
@Qualifier("points-to-osrm-elevations-coordinates-converter")
public class PointsToOSRMElevationsCoordinatesConverter implements PointsToCoordinatesConverter {

    // 37.696012,55.620324|37.705061,55.623574 - long,lat|long,lat
    @Override
    public String convertPointsToCoordinates(List<Point> points) {
        return points.stream().map(p -> p.longitude() + "," + p.latitude()).collect(Collectors.joining("|"));
    }
}
