package ru.elistratov.supplier.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.converter.PointsToOSRMCoordinatesConverter;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;
import ru.elistratov.supplier.jsommapper.OsrmJsonMapper;
import ru.elistratov.webclient.OsrmWebClient;

/*
       Using block method while handling getRoute response - blocking method, non-reflective
*/
@Component
@Slf4j
@AllArgsConstructor
public class DistancesGetterImpl implements DistancesGetter {
    private final PointsToOSRMCoordinatesConverter converter;
    private final OsrmJsonMapper osrmJsonMapper;
    private final OsrmWebClient httpClient;

    public float getDistanceForRoute(Route route) throws WebClientException, JsonProcessingException {
        List<Point> points = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(Waypoint::point)
                .toList();
        String coordinates = converter.convertPointsToCoordinates(points);
        final String jsonStr = httpClient.getRoute(coordinates).block();
        return osrmJsonMapper.getDistanceFromJson(jsonStr);
    }
}
