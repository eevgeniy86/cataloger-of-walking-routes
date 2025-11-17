package ru.elistratov.webclient;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.elistratov.exception.WebClientException;

@Service
public class OsrmWebClientImpl implements OsrmWebClient {

    private final WebClient webClient;
    private final String token;
    public final String url;
    private static final int timeout = 10_000;

    public OsrmWebClientImpl(
            WebClient webClient, @Value("${osrm.api.token}") String token, @Value("${osrm.api.url}") String url) {
        this.webClient = webClient;
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
                .timeout(Duration.ofMillis(timeout))
                .onErrorMap(WebClientException::new)
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)));
    }
}
