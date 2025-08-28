package ru.otus.webclient;

import reactor.core.publisher.Mono;

public interface OsrmHttpClient {
    Mono<String> getRoute(String coordinates);
}
