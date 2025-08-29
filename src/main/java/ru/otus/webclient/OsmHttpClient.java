package ru.otus.webclient;

import reactor.core.publisher.Mono;

public interface OsmHttpClient {
    Mono<String> getPublicTransportStops(String coordinates);
}
