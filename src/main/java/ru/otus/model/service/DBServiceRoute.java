package ru.otus.model.service;

import java.util.Optional;
import ru.otus.model.domain.Route;

public interface DBServiceRoute {

    Route saveRoute(Route route);

    Optional<Route> getRoute(long id);
}
