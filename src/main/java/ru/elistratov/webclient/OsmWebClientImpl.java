package ru.elistratov.webclient;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import ru.elistratov.exception.WebClientException;

@Service
public class OsmWebClientImpl implements OsmWebClient {
    private final WebClient webClient;
    private final String url;

    public OsmWebClientImpl(WebClient webClient, @Value("${osm.api.url}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    public Flux<String> getPublicTransportStops(String coordinates) {

        // 55.7558,37.6176 - lat,long
        String body = String.format(
                """
                        [out:json][timeout:180];
                        (
                          node(around:1000,%1$s)[highway="bus_stop"]; //остановка с павильоном
                          node(around:1000,%1$s)[railway="station"]; //жд станция (грузовая или пассажирская)
                        );
                        out;""",
                coordinates);

        return webClient
                .post()
                .uri(url)
                .accept(MediaType.ALL)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorMap(WebClientException::new)
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)));
    }
}
