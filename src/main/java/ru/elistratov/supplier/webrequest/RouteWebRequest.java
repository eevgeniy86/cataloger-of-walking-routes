package ru.elistratov.webrequest;

import reactor.core.publisher.Mono;

public interface RouteWebRequest {
    Mono<String> getRoute(String coordinates);
}
