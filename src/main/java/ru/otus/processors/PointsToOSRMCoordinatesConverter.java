package ru.otus.processors;

import java.util.List;
import java.util.stream.Collectors;
import ru.otus.model.domain.Point;

public class PointsToOSRMCoordinatesConverter {

    // 37.696012,55.620324;37.705061,55.623574
    public static String convertPointsToCoordinates(List<Point> points) {
        return points.stream().map(p -> p.longitude() + "," + p.latitude()).collect(Collectors.joining(";"));
    }
}
