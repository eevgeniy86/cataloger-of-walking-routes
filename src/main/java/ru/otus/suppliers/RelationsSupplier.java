package ru.otus.suppliers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.Converters.PointsToOSMCoordinatesConverter;
import ru.otus.exceptions.JsonParsingException;
import ru.otus.exceptions.WebClientException;
import ru.otus.model.domain.*;
import ru.otus.service.DBServiceRelation;
import ru.otus.service.DBServiceRoute;
import ru.otus.service.DBServiceStation;
import ru.otus.webclient.OsmWebClient;

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class RelationsSupplier {

    private final PointsToOSMCoordinatesConverter converter;
    private final DBServiceRoute dbRoute;
    private final DBServiceRelation dbRelation;
    private final DBServiceStation dbStation;

    private final OsmWebClient httpClient;

    private static final String busStopKey = "highway";
    private static final String busStopValue = "bus_stop";
    private static final String railStopKey = "railway";
    private static final String railStopValue = "station";
    private static final String subwayKey = "subway";
    private static final String trainKey = "train";

    private static Set<Long> stationIds;

    /*
        Using reduce to mono method while handling getPublicTransportStops response,
        instead of working with flux response,
        because Overpass api chunked response contains string parts, not valid json-objects
    */
    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {

        if (stationIds == null) {
            getStationIdsFromDb();
        }
        List<Route> routesWithoutRelations = getRoutesWithoutRelations();
        for (Route route : routesWithoutRelations) {
            try {
                List<Relation> relations = getRelationsToRoute(route);
                saveRelationsToRoute(route, relations);
            } catch (WebClientException | JsonParsingException e) {
                saveErrorForRoute(route, e);
                stationIds = null;
            }
        }
    }

    private void saveRelationsToRoute(Route route, List<Relation> relations) {
        for (Relation r : relations) {
            dbRelation.saveRelation(r);
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

    private void saveErrorForRoute(Route route, Exception e) {
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

    private List<Route> getRoutesWithoutRelations() {
        List<Route> routesWithoutRelations =
                dbRoute.getRoutesByRelationsProcessingStatus(RelationsProcessingStatus.NOT_PROCESSED);
        log.atInfo()
                .setMessage("Get routes without relations: {}")
                .addArgument(routesWithoutRelations)
                .log();
        return routesWithoutRelations;
    }

    private void getStationIdsFromDb() {
        stationIds = dbStation.getAllIds();
    }

    private List<Relation> getRelationsToRoute(Route route) throws WebClientException, JsonParsingException {
        List<Point> points = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(Waypoint::point)
                .toList();
        List<Relation> relations = new ArrayList<>();
        relations.add(new Relation(null, route.id(), RelationType.START_FINISH, null, null));
        for (Station s : getStationsToPoint(points.getFirst())) {
            relations.add(new Relation(null, route.id(), RelationType.START_STATION, null, s));
        }
        for (Station s : getStationsToPoint(points.getLast())) {
            relations.add(new Relation(null, route.id(), RelationType.FINISH_STATION, null, s));
        }
        return relations;
    }

    private List<Station> getStationsToPoint(Point point) throws WebClientException, JsonParsingException {
        String coordinates = converter.convertPointToCoordinates(point);
        List<Station> stations;
        String jsonStr = httpClient
                .getPublicTransportStops(coordinates)
                .reduce("", (a, b) -> a + b)
                .block();
        try {
            stations = new ArrayList<>(getStationsFromJson((jsonStr)));
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(e.getMessage());
        }
        return stations;
    }

    private List<Station> getStationsFromJson(String jsonStr) throws JsonProcessingException {
        List<Station> result = new ArrayList<>();
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
                result.add(
                        new Station(id, name, stationType, new Point(null, latitude, longitude), stationIds.add(id)));
            }
        }
        System.out.println(result);
        return result;
    }
}
