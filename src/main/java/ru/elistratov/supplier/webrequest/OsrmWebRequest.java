package ru.elistratov.supplier.webrequest;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class OsrmWebRequest implements RouteWebRequest, ElevationsWebRequest {

    private static final int TIMEOUT = 10_000;
    private final WebClient webClient;
    private final RateLimiter osrmRateLimiter;
    private final String token;
    private final String url;
    private final String routeUri;
    private final String elevationsUri;

    public OsrmWebRequest(
            WebClient webClient,
            RateLimiter osrmRateLimiter,
            @Value("${osrm.api.token}") String token,
            @Value("${osrm.api.url}") String url,
            @Value("${osrm.api.route-uri}") String routeUri,
            @Value("${osrm.api.elevations-uri}") String elevationsUri) {
        this.webClient = webClient;
        this.osrmRateLimiter = osrmRateLimiter;
        this.token = token;
        this.url = url;
        this.routeUri = routeUri;
        this.elevationsUri = elevationsUri;
    }

    public Mono<String> getRoute(String coordinates) {
        String urlRequest =
                String.format("%1$s%2$s/%3$s?access-token=%4$s&overview=false", url, routeUri, coordinates, token);
        return webClient
                .get()
                .uri(urlRequest)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(TIMEOUT))
                .doOnSuccess(s -> log.atInfo()
                        .setMessage("Got success response from {}")
                        .addArgument(urlRequest)
                        .log())
                .doOnError(e -> log.atError().setMessage(e.getMessage()).log())
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)))
                .transformDeferred(RateLimiterOperator.of(osrmRateLimiter));
    }

    // GET
    // https://api.jawg.io/elevations?samples=1024&path=37.777010,55.420065%7C37.717528,55.395522&access-token=***
    @Override
    public Flux<String> getElevations(String coordinates, int samples) {
        String urlRequest = String.format(
                "%1$s%2$s?samples=%3$d&path=%4$s&access-token=%5$s", url, elevationsUri, samples, coordinates, token);
        return webClient
                .get()
                .uri(urlRequest)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofMillis(TIMEOUT))
                .doOnError(e -> log.atError().setMessage(e.getMessage()).log())
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)))
                .doOnComplete(() -> log.atInfo()
                        .setMessage("Got success response from: {}")
                        .addArgument(urlRequest)
                        .log())
                .transformDeferred(RateLimiterOperator.of(osrmRateLimiter));
    }
}
