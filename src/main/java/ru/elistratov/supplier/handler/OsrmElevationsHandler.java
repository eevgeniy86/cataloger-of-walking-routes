package ru.elistratov.supplier.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.elistratov.converter.PointsToCoordinatesConverter;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;
import ru.elistratov.supplier.jsonmapper.JsonToElevationsMapper;
import ru.elistratov.supplier.saver.ElevationsSaver;
import ru.elistratov.supplier.webrequest.ElevationsWebRequest;

@Component
@Slf4j
@Qualifier("osrm-elevations-handler")
public class OsrmElevationsHandler implements RouteHandler {
    private static final float metersBetweenPoints = 5.0f;
    private static final int maxSamplesNumber = 1024;
    private final PointsToCoordinatesConverter converter;
    private final JsonToElevationsMapper jsonMapper;
    private final ElevationsWebRequest request;
    private final ElevationsSaver saver;

    public OsrmElevationsHandler(
            @Qualifier("points-to-osrm-elevations-coordinates-converter") PointsToCoordinatesConverter converter,
            JsonToElevationsMapper jsonMapper,
            ElevationsWebRequest request,
            ElevationsSaver saver) {
        this.converter = converter;
        this.jsonMapper = jsonMapper;
        this.request = request;
        this.saver = saver;
    }

    @Override
    public void handleRoute(Route route) {
        List<Waypoint> waypoints = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .toList();
        Map<List<Waypoint>, Float> waypointSeries = getWaypointsSeries(waypoints);
        int seriesCount = waypointSeries.size();
        for (List<Waypoint> series : waypointSeries.keySet()) {
            List<Point> points = series.stream().map(Waypoint::point).toList();
            String coordinates = converter.convertPointsToCoordinates(points);
            int samplesNumber = (int) Math.ceil(waypointSeries.get(series) / metersBetweenPoints) + 1;
            short lastWaypointIndex = series.getLast().index();
            samplesNumber = Math.min(samplesNumber, maxSamplesNumber);
            request.getElevations(coordinates, samplesNumber)
                    .reduce("", (a, b) -> a + b)
                    .subscribe(s -> handleWebResponse(s, lastWaypointIndex, seriesCount, route));
        }
        log.atInfo().setMessage("Route handled: {}").addArgument(route).log();
    }

    private Map<List<Waypoint>, Float> getWaypointsSeries(List<Waypoint> waypoints) {
        Map<List<Waypoint>, Float> waypointSeries = new HashMap<>();
        float maxLength = metersBetweenPoints * maxSamplesNumber;
        List<Waypoint> provisional = new LinkedList<>();
        float length = 0;
        for (Waypoint w : waypoints) {
            provisional.add(w);
            if (w.distanceToNext() == null) {
                waypointSeries.put(List.copyOf(provisional), length);
            } else if (provisional.size() >= 2 && length + w.distanceToNext() >= maxLength) {
                waypointSeries.put(List.copyOf(provisional), length);
                provisional = new LinkedList<>();
                provisional.add(w);
                length = w.distanceToNext();
            } else {
                length += w.distanceToNext();
            }
        }
        return waypointSeries;
    }

    private void handleWebResponse(String response, short lastWaypointIndex, int seriesCount, Route route) {
        try {
            SequencedMap<Point, Float> elevations = jsonMapper.getElevationsFromJson(response);
            saver.saveResultToRoute(elevations, lastWaypointIndex, seriesCount, route.id());
        } catch (JsonProcessingException jpe) {
            log.atError().setMessage(jpe.getMessage()).log();
        }
    }
}
