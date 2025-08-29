package ru.otus.suppliers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.Converters.PointsToOSMCoordinatesConverter;
import ru.otus.exceptions.WebClientException;
import ru.otus.model.domain.*;
import ru.otus.service.DBServiceRelation;
import ru.otus.service.DBServiceRoute;
import ru.otus.webclient.OsmWebClient;

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class RelationsSupplier {
    private final DBServiceRoute dbRoute;
    private final DBServiceRelation dbRelation;
    private final OsmWebClient httpClient;
    private static final String busStopKey = "highway";
    private static final String busStopValue = "bus_stop";
    private static final String railStopKey = "railway";
    private static final String railStopValue = "station";
    private static final String subwayKey = "subway";
    private static final String trainKey = "train";

    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {

        List<Route> routesWithoutRelations =
                dbRoute.getRoutesByRelationsProcessingStatus(RelationsProcessingStatus.NOT_PROCESSED);
        log.atInfo()
                .setMessage("Get unprocessed routes: {}")
                .addArgument(routesWithoutRelations)
                .log();

        for (Route route : routesWithoutRelations) {

            List<Point> points = route.waypointsList().stream()
                    .sorted(Comparator.comparingInt(Waypoint::index))
                    .map(Waypoint::point)
                    .toList();

            Point start = points.getFirst();
            Point finish = points.getLast();
            String startCoordinates = PointsToOSMCoordinatesConverter.convertPointToCoordinates(start);
            String finishCoordinates = PointsToOSMCoordinatesConverter.convertPointToCoordinates(finish);
            final String[] jsonStrings = new String[2];

            try {
                jsonStrings[0] = httpClient.getPublicTransportStops(startCoordinates).reduce("", (a, b) -> a + b).block();
                httpClient.getPublicTransportStops(finishCoordinates).subscribe(jsonStr -> jsonStrings[1] = jsonStr);
                log.atInfo().setMessage("ALARM {}").addArgument(jsonStrings).log();
                saveRelationsToRoute(route, start, finish, jsonStrings);

            } catch (WebClientException | JsonProcessingException e) {
                log.atError()
                        .setMessage("Error while getting stations for {}, message: {}")
                        .addArgument(route)
                        .addArgument(e.getMessage())
                        .log();
                Route newRoute = new Route(
                        route.id(),
                        route.name(),
                        route.description(),
                        route.waypointsNumber(),
                        route.waypointsList(),
                        route.length(),
                        route.ascent(),
                        route.descent(),
                        RelationsProcessingStatus.STATIONS_ERROR);
                dbRoute.saveRoute(newRoute);
            }
            Route newRoute = new Route(
                    route.id(),
                    route.name(),
                    route.description(),
                    route.waypointsNumber(),
                    route.waypointsList(),
                    route.length(),
                    route.ascent(),
                    route.descent(),
                    RelationsProcessingStatus.STATIONS_GET);
            dbRoute.saveRoute(newRoute);
            log.atInfo()
                    .setMessage("Relations added for route {}")
                    .addArgument(newRoute)
                    .log();
        }
    }

    private void saveRelationsToRoute(Route route, Point start, Point finish, String[] jsonStrings)
            throws JsonProcessingException {

        List<Relation> relations = new ArrayList<>();
        relations.add(new Relation(null, route.id(), RelationType.START_FINISH, start, finish, null, null));

        // public Station(Long id, String name, String network, StationType type, boolean isNew)

        List<Relation> relationsStart =
                getRelationsFromJson(jsonStrings[0], route.id(), start, RelationType.START_STATION);
        List<Relation> stationsFinish =
                getRelationsFromJson(jsonStrings[1], route.id(), start, RelationType.FINISH_STATION);
        relations.addAll(relationsStart);
        relations.addAll(stationsFinish);
        dbRelation.saveAll(relations);
    }

    private List<Relation> getRelationsFromJson(
            String jsonStr, Long routeId, Point firstPoint, RelationType relationType) throws JsonProcessingException {
        List<Relation> result = new ArrayList<>();
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        for (Iterator<JsonNode> it = node.get("elements").elements(); it.hasNext(); ) {
            JsonNode element = it.next();
            Long id = element.get("id").asLong();
            double latitude = element.get("lat").asDouble();
            double longitude = element.get("lon").asDouble();
            JsonNode tags = element.get("tags");
            String name = tags.get("name").asText();
            StationType stationType = null;
            if (tags.has(busStopKey) && tags.get(busStopKey).asText().equals(busStopValue)) {
                stationType = StationType.BUS;
            } else if (tags.has(railStopKey) && tags.get(railStopKey).asText().equals(railStopValue)) {
                if (tags.has(subwayKey) && tags.get(subwayKey).asBoolean()) {
                    stationType = StationType.SUBWAY;
                } else if (tags.has(trainKey) && tags.get(trainKey).asBoolean()) {
                    stationType = StationType.TRAIN;
                }
            }
            if (stationType != null) {
                result.add(new Relation(
                        null,
                        routeId,
                        relationType,
                        firstPoint,
                        new Point(null, latitude, longitude, null),
                        new Station(id, name, null, stationType, true),
                        null));
            }
        }

        return result;
    }
}
