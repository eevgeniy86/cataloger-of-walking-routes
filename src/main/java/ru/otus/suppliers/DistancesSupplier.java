package ru.otus.suppliers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.Converters.PointsToOSRMCoordinatesConverter;
import ru.otus.exceptions.WebClientException;
import ru.otus.model.domain.Point;
import ru.otus.model.domain.Route;
import ru.otus.model.domain.Waypoint;
import ru.otus.service.DBServiceRoute;
import ru.otus.webclient.OsrmWebClient;

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class DistancesSupplier {
    private final DBServiceRoute dbService;
    private final OsrmWebClient httpClient;

    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {

        List<Route> unprocessedRoutes = dbService.getRoutesWithNoDistance();
        log.atInfo()
                .setMessage("Get unprocessed routes: {}")
                .addArgument(unprocessedRoutes)
                .log();

        for (Route route : unprocessedRoutes) {

            List<Point> points = route.waypointsList().stream()
                    .sorted(Comparator.comparingInt(Waypoint::index))
                    .map(Waypoint::point)
                    .toList();
            String coordinates = PointsToOSRMCoordinatesConverter.convertPointsToCoordinates(points);

            try {
                httpClient.getRoute(coordinates).subscribe(jsonStr -> saveDistanceToRoute(jsonStr, route));
            } catch (WebClientException e) {
                log.atError()
                        .setMessage("Error while getting route distances for {}")
                        .addArgument(route)
                        .log();
            }
        }
    }

    private void saveDistanceToRoute(String jsonStr, Route route) {
        Float distance = null;
        try {
            distance = getDistanceFromJson(jsonStr);
        } catch (JsonProcessingException jpe) {
            log.atError()
                    .setMessage("Error while getting distance from json: {}")
                    .addArgument(jsonStr)
                    .log();
        }
        Route newRoute = new Route(
                route.id(),
                route.name(),
                route.description(),
                route.waypointsNumber(),
                route.waypointsList(),
                distance,
                route.ascent(),
                route.descent(),
                route.relationsProcessingStatus());
        dbService.saveRoute(newRoute);
        log.atInfo()
                .setMessage("Distance saved for route {}")
                .addArgument(newRoute)
                .log();
    }

    private float getDistanceFromJson(String jsonStr) throws JsonProcessingException {
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        return (float) node.get("routes").get(0).get("distance").asDouble();
    }
}
