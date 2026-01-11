package ru.elistratov.supplier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;
import ru.elistratov.service.DBServiceRoute;

import java.util.Comparator;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class OsrmDistancesSaver implements DistancesSaver {
    private final DBServiceRoute dbService;

    @Override
    public void saveResultToRoute(Deque<Float> distances, Route route) {
        Set<Waypoint> waypoints = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(w -> new Waypoint(w.id(), w.index(), w.comment(), w.point(), distances.pollFirst()))
                .collect(Collectors.toSet());
        dbService.saveRoute(new Route(route.id(), route.name(), route.description(), route.waypointsNumber(), waypoints, route.length(), route.ascent(), route.descent(), route.relationsProcessingStatus()));
        Route saved = dbService.saveRoute(route);
        log.atInfo()
                .setMessage("Distance saved for route {}")
                .addArgument(saved)
                .log();

    }
}
