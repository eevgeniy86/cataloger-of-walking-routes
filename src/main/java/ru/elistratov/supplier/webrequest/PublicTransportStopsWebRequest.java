package ru.elistratov.webrequest;

import reactor.core.publisher.Flux;

public interface PublicTransportStopsWebRequest {
    Flux<String> getPublicTransportStops(String coordinates);
}
