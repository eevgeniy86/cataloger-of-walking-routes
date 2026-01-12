package ru.elistratov.supplier;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.elistratov.db.service.DBServiceRoute;
import ru.elistratov.model.domain.Route;
import ru.elistratov.supplier.handler.RouteHandler;

@Component
@EnableScheduling
@Slf4j
public class DistancesSupplier {
    private final DBServiceRoute dbService;
    private final RouteHandler routeHandler;

    public DistancesSupplier(DBServiceRoute dbService, @Qualifier("osrmDistancesHandler") RouteHandler routeHandler) {
        this.dbService = dbService;
        this.routeHandler = routeHandler;
    }

    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {
        List<Route> unprocessedRoutes = dbService.getRoutesWithNoDistance();
        log.atInfo()
                .setMessage("Get routes with no distances: {}")
                .addArgument(unprocessedRoutes)
                .log();
        for (Route route : unprocessedRoutes) {
            routeHandler.handleRoute(route);
            log.atInfo()
                    .setMessage("Route sent for distance handling: {}")
                    .addArgument(route)
                    .log();
        }
    }
}
