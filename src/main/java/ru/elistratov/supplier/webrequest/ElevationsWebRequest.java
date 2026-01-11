package ru.elistratov.webrequest;

import reactor.core.publisher.Flux;

public interface ElevationsWebRequest {
    Flux<String> getElevations(String coordinates);
}
