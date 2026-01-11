package ru.elistratov.supplier.saver;

import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.db.service.DBServiceRoute;
import ru.elistratov.db.service.DBServiceWaypoint;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;

@Component
@Slf4j
@AllArgsConstructor
public class DistancesSaverImpl implements DistancesSaver {
    private final DBServiceRoute dbServiceRoute;
    private final DBServiceWaypoint dbServiceWaypoint;

    @Override
    public void saveResultToRoute(Deque<Float> distances, Route route) {
        float totalLength = 0;
        for (Float d : distances) {
            if (d != null) totalLength += d;
        }
        List<Waypoint> waypoints = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(w -> new Waypoint(w.id(), w.index(), w.comment(), w.point(), distances.pollFirst()))
                .collect(Collectors.toList());
        int result = dbServiceWaypoint.updateWaypointsWithDistances(waypoints);
        if (result == waypoints.size()) {
            Route updated = dbServiceRoute.updateRouteWithLength(route.id(), totalLength);
            log.atInfo()
                    .setMessage("Distances saved for route {}")
                    .addArgument(updated)
                    .log();
        }
    }
}
