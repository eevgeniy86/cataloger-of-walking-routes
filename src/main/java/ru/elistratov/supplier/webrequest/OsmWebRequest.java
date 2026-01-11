package ru.elistratov.webrequest;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class OsmWebRequest implements PublicTransportStopsWebRequest {
    private static final int TIMEOUT = 10_000;
    private final WebClient webClient;
    private final RateLimiter osmRateLimiter;
    private final String url;

    public OsmWebRequest(WebClient webClient, RateLimiter osmRateLimiter, @Value("${osm.api.url}") String url) {
        this.webClient = webClient;
        this.osmRateLimiter = osmRateLimiter;
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
                .timeout(Duration.ofMillis(TIMEOUT))
                .doOnError(e -> log.atError().setMessage(e.getMessage()).log())
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)))
                .doOnComplete(() -> log.atInfo().setMessage("Got success response for coordinates: {}").addArgument(coordinates).log())
                .transformDeferred(RateLimiterOperator.of(osmRateLimiter));
    }
}
