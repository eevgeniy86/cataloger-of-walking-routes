package ru.otus.model.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.model.domain.Route;

public interface DBServiceRoute {

    Mono<Route> saveRoute(Route route);

    Mono<Route> getRoute(long id);

    Flux<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent);

    Flux<Route> getRoutesWithNoDistance();
}
