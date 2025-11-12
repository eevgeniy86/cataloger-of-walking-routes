package ru.elistratov.supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.Route;
import ru.elistratov.service.DBServiceRoute;
import ru.elistratov.supplier.helper.DistancesGetter;

/*
TODO: split this class:
-json handling
-routes saver
-relations handler
*/

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class DistancesSupplier {
    private final DBServiceRoute dbService;
    private final DistancesGetter distancesGetter;

    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {

        List<Route> unprocessedRoutes = getRoutesWithoutDistances();
        for (Route route : unprocessedRoutes) {
            try {
                float distance = distancesGetter.getDistanceForRoute(route);
                saveDistanceToRoute(distance, route);
            } catch (WebClientException | JsonProcessingException e) {
                log.atError()
                        .setMessage("Error while getting route distances for {}")
                        .addArgument(route)
                        .log();
            }
        }
    }

    private List<Route> getRoutesWithoutDistances() {
        List<Route> unprocessedRoutes = dbService.getRoutesWithNoDistance();
        log.atInfo()
                .setMessage("Get unprocessed routes: {}")
                .addArgument(unprocessedRoutes)
                .log();
        return unprocessedRoutes;
    }

    private void saveDistanceToRoute(float distance, Route route) {

        Route newRoute = dbService.updateRouteWithLength(route, distance);
        log.atInfo()
                .setMessage("Distance saved for route {}")
                .addArgument(newRoute)
                .log();
    }
}
