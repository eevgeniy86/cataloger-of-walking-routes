package ru.elistratov.supplier;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.elistratov.db.service.DBServiceRoute;
import ru.elistratov.model.domain.*;
import ru.elistratov.supplier.handler.RouteHandler;

@Component
@EnableScheduling
@Slf4j
public class RelationsSupplier {

    private final DBServiceRoute dbService;
    private final RouteHandler routeHandler;

    public RelationsSupplier(DBServiceRoute dbService, @Qualifier("osmRelationsHandler") RouteHandler routeHandler) {
        this.dbService = dbService;
        this.routeHandler = routeHandler;
    }

    @Scheduled(fixedDelay = 300_000)
    @Async
    public void start() {
        List<Route> routesWithoutRelations =
                dbService.getRoutesByRelationsProcessingStatus(RelationsProcessingStatus.NOT_PROCESSED);
        log.atInfo()
                .setMessage("Get routes without relations: {}")
                .addArgument(routesWithoutRelations)
                .log();
        for (Route route : routesWithoutRelations) {
            routeHandler.handleRoute(route);
            log.atInfo()
                    .setMessage("Route sent for relations handling: {}")
                    .addArgument(route)
                    .log();
        }
    }
}
