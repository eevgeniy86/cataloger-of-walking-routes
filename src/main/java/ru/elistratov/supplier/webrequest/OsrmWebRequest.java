package ru.elistratov.webrequest;

import java.time.Duration;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class OsrmWebRequest implements RouteWebRequest {

    private static final int TIMEOUT = 10_000;
    private final WebClient webClient;
    private final RateLimiter osrmRateLimiter;
    private final String token;
    private final String url;

    public OsrmWebRequest(
            WebClient webClient, RateLimiter osrmRateLimiter, @Value("${osrm.api.token}") String token, @Value("${osrm.api.url}") String url) {
        this.webClient = webClient;
        this.osrmRateLimiter = osrmRateLimiter;
        this.token = token;
        this.url = url;
    }

    // GET
    // https://api.jawg.io/routing/route/v1/walk/37.696012,55.620324;37.705061,55.623574?access-token=AVnGv0IRBhBGetUGtYJyOU19pSRGxus7gBHYhBktiTWXikj77IA1GfMwQzM7xo9a&overview=false}
    public Mono<String> getRoute(String coordinates) {

        String urlRequest = String.format("%1$s/%2$s?access-token=%3$s&overview=false", url, coordinates, token);

        return webClient
                .get()
                .uri(urlRequest)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(TIMEOUT))
                .doOnSuccess(s -> log.atInfo().setMessage("Got success response from {}").addArgument(urlRequest).log())
                .doOnError(e -> log.atError().setMessage(e.getMessage()).log())
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)))
                .transformDeferred(RateLimiterOperator.of(osrmRateLimiter));
    }

    //    @Override
    //    public Flux<String> getElevations(String coordinates) {
    //
    //        // 55.7558,37.6176 - lat,long
    //        String body = String.format(
    //                """
    //                        [out:json][timeout:180];
    //                        (
    //                          node(around:1000,%1$s)[highway="bus_stop"]; //остановка с павильоном
    //                          node(around:1000,%1$s)[railway="station"]; //жд станция (грузовая или пассажирская)
    //                        );
    //                        out;""",
    //                coordinates);
    //
    //        return webClient
    //                .post()
    //                .uri(url)
    //                .accept(MediaType.ALL)
    //                .contentType(MediaType.TEXT_PLAIN)
    //                .bodyValue(body)
    //                .retrieve()
    //                .bodyToFlux(String.class)
    //                .onErrorMap(WebClientException::new)
    //                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)));
    //    }
}
