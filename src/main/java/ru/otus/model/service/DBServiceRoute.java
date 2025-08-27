package ru.otus.model.service;

import java.util.List;
import java.util.Optional;
import ru.otus.model.domain.Route;

public interface DBServiceRoute {

    Route saveRoute(Route route);

    Optional<Route> getRoute(long id);

    List<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent);
}
