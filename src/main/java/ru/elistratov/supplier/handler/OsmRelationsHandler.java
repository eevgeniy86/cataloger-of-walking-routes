package ru.elistratov.supplier.getter;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.converter.PointToCoordinatesConverter;
import ru.elistratov.model.domain.*;
import ru.elistratov.supplier.RelationsSaver;
import ru.elistratov.supplier.jsonmapper.JsonToStationsMapper;
import ru.elistratov.webrequest.OsmWebRequest;

@Component
@Slf4j
@AllArgsConstructor
public class OsmRelationsHandler implements RelationsHandler {
    private final OsmWebRequest request;
    private final PointToCoordinatesConverter converter;
    private final RelationsSaver saver;
    private final JsonToStationsMapper jsonMapper;

    @Override
    public void handleRoute(Route route) {
        List<Point> points = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(Waypoint::point)
                .toList();
        saver.saveResultToRoute(null, RelationType.START_FINISH, route.id());
        String coordinatesFirst = converter.convertPointToCoordinates(points.getFirst());
        request.getPublicTransportStops(coordinatesFirst)
                .reduce("", (a, b) -> a + b)
                .subscribe(s -> handleWebResponse(s, RelationType.START_STATION, route.id()));
        String coordinatesLast = converter.convertPointToCoordinates(points.getLast());
        request.getPublicTransportStops(coordinatesLast)
                .reduce("", (a, b) -> a + b)
                .subscribe(s -> handleWebResponse(s, RelationType.FINISH_STATION, route.id()));
        log.atInfo().setMessage("Route handled: {}").addArgument(route).log();
    }

    private void handleWebResponse(String response, RelationType type, long routeId) {
        try {
            List<Station> stations = jsonMapper.getStationsFromJson(response);
            saver.saveResultToRoute(stations, type, routeId);
        } catch (JsonProcessingException jpe) {
            log.atError().setMessage(jpe.getMessage()).log();
        }
    }

}
