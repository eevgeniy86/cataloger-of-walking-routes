package ru.elistratov.supplier.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.elistratov.converter.PointsToCoordinatesConverter;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;
import ru.elistratov.supplier.jsonmapper.JsonToDistancesMapper;
import ru.elistratov.supplier.saver.DistancesSaver;
import ru.elistratov.supplier.webrequest.RouteWebRequest;

@Component
@Slf4j
@Qualifier("osrmDistancesHandler")
public class OsrmDistancesHandler implements RouteHandler {
    private final PointsToCoordinatesConverter converter;
    private final JsonToDistancesMapper jsonMapper;
    private final RouteWebRequest request;
    private final DistancesSaver saver;

    public OsrmDistancesHandler(
            @Qualifier("pointsToOsrmRouteCoordinatesConverter") PointsToCoordinatesConverter converter,
            JsonToDistancesMapper jsonMapper,
            RouteWebRequest request,
            DistancesSaver saver) {
        this.converter = converter;
        this.jsonMapper = jsonMapper;
        this.request = request;
        this.saver = saver;
    }

    @Override
    public void handleRoute(Route route) {
        List<Point> points = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(Waypoint::point)
                .toList();
        String coordinates = converter.convertPointsToCoordinates(points);
        request.getRoute(coordinates).subscribe(s -> handleWebResponse(s, route));
        log.atInfo().setMessage("Route handled: {}").addArgument(route).log();
    }

    private void handleWebResponse(String response, Route route) {
        try {
            Deque<Float> distances = jsonMapper.getDistancesFromJson(response);
            saver.saveResultToRoute(distances, route);
        } catch (JsonProcessingException jpe) {
            log.atError().setMessage(jpe.getMessage()).log();
        }
    }
}
