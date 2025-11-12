package ru.elistratov.webclient;

import reactor.core.publisher.Flux;

public interface OsmWebClient {
    Flux<String> getPublicTransportStops(String coordinates);
}
