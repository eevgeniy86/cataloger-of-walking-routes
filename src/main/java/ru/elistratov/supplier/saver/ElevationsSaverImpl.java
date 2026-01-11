package ru.elistratov.supplier.saver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.db.service.DBServiceRoute;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Route;

@Component
@Slf4j
@AllArgsConstructor
public class ElevationsSaverImpl implements ElevationsSaver {
    private final DBServiceRoute dbService;
    private final Map<Long, Map<Short, SequencedMap<Point, Float>>> unsavedElevations = new ConcurrentSkipListMap<>();

    @Override
    public void saveResultToRoute(
            SequencedMap<Point, Float> elevations, short lastWaypointIndex, int seriesCount, long routeId) {
        if (!unsavedElevations.containsKey(routeId)) {
            unsavedElevations.put(routeId, new ConcurrentHashMap<>());
        }
        Map<Short, SequencedMap<Point, Float>> routeElevationsSeries = unsavedElevations.get(routeId);
        routeElevationsSeries.put(lastWaypointIndex, elevations);
        if (routeElevationsSeries.size() == seriesCount) {
            List<Float> elevationsSequence = getElevationsSequence(routeElevationsSeries);
            calculateAndSaveSlopesForRoute(elevationsSequence, routeId);
            unsavedElevations.remove(routeId);
        }
    }
    /*
    Boundary points of series will be duplicated, but it is no matter (slopes is 0 for them)
     */
    private List<Float> getElevationsSequence(Map<Short, SequencedMap<Point, Float>> elevationsSeries) {
        List<Float> elevationsSequence = new ArrayList<>();
        for (short index : elevationsSeries.keySet().stream().sorted().toList()) {
            elevationsSequence.addAll(elevationsSeries.get(index).sequencedValues());
        }
        return elevationsSequence;
    }

    private void calculateAndSaveSlopesForRoute(List<Float> elevationsSequence, long routeId) {
        float ascent = 0;
        float descent = 0;
        float previous = elevationsSequence.getFirst();
        for (float elevation : elevationsSequence) {
            if (elevation - previous >= 0) {
                ascent += elevation - previous;
            } else {
                descent -= elevation - previous;
            }
            previous = elevation;
        }
        Route saved = dbService.updateRouteWithSlopes(routeId, ascent, descent);
        log.atInfo().setMessage("Slopes saved for route: {}").addArgument(saved).log();
    }
}
