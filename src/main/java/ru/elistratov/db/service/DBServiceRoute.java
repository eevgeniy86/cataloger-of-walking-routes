package ru.elistratov.service;

import java.util.List;
import java.util.Optional;
import ru.elistratov.model.domain.RelationsProcessingStatus;
import ru.elistratov.model.domain.Route;

public interface DBServiceRoute {

    Route saveRoute(Route route);

    Optional<Route> getRoute(long id);

    List<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent);

    List<Route> getRoutesWithNoDistance();

    List<Route> getRoutesByRelationsProcessingStatus(RelationsProcessingStatus status);

    Route updateRouteWithStatus(long routeId, RelationsProcessingStatus status);
}
