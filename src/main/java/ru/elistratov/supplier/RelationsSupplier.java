package ru.elistratov.supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.*;
import ru.elistratov.service.DBServiceRelation;
import ru.elistratov.service.DBServiceRoute;
import ru.elistratov.service.DBServiceStation;
import ru.elistratov.supplier.helper.StationsGetter;

/*
TODO: split this class:
-routes saver
-relations handler
 */

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class RelationsSupplier {

    private final DBServiceRoute dbRoute;
    private final DBServiceRelation dbRelation;
    private final DBServiceStation dbStation;
    private final StationsGetter stationsGetter;

    private static Set<Long> stationIds;

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
            } catch (WebClientException | JsonProcessingException e) {
                saveErrorForRoute(route, e);
                stationIds = null;
            }
        }
    }

    @Transactional
    private void saveRelationsToRoute(Route route, List<Relation> relations) {
        for (Relation r : relations) {
            dbRelation.saveRelation(r);
        }
        Route newRoute = dbRoute.updateRouteWithStatus(route, RelationsProcessingStatus.STATIONS_GET);
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
        dbRoute.updateRouteWithStatus(route, RelationsProcessingStatus.STATIONS_ERROR);
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

    private List<Relation> getRelationsToRoute(Route route) throws WebClientException, JsonProcessingException {
        List<Point> points = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(Waypoint::point)
                .toList();
        List<Relation> relations = new ArrayList<>();
        relations.add(new Relation(null, route.id(), RelationType.START_FINISH, null, null));
        for (Station s : stationsGetter.getStationsToPoint(points.getFirst())) {
            s.setIsNew(stationIds.add(s.getId()));
            relations.add(new Relation(null, route.id(), RelationType.START_STATION, null, s));
        }
        for (Station s : stationsGetter.getStationsToPoint(points.getLast())) {
            s.setIsNew(stationIds.add(s.getId()));
            relations.add(new Relation(null, route.id(), RelationType.FINISH_STATION, null, s));
        }
        return relations;
    }
}
