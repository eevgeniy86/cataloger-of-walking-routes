package ru.otus.webclient;

import reactor.core.publisher.Mono;

public interface OsrmWebClient {
    Mono<String> getRoute(String coordinates);
}
