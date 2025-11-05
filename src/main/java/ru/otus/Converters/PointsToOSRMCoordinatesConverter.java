package ru.otus.Converters;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.otus.model.domain.Point;

@Component
public class PointsToOSRMCoordinatesConverter {

    // 37.696012,55.620324;37.705061,55.623574 - long,lat;long,lat
    public String convertPointsToCoordinates(List<Point> points) {
        return points.stream().map(p -> p.longitude() + "," + p.latitude()).collect(Collectors.joining(";"));
    }
}
