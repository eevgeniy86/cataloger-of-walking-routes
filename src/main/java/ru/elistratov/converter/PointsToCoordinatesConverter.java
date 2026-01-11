package ru.elistratov.converter;

import java.util.List;
import ru.elistratov.model.domain.Point;

public interface PointsToCoordinatesConverter {
    String convertPointsToCoordinates(List<Point> points);
}
