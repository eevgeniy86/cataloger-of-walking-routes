package ru.otus.webclient;

import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.otus.exceptions.WebClientException;

@Service
@AllArgsConstructor
public class OsrmWebClientImpl implements OsrmWebClient {

    private final WebClient webClient;
    private static final String token = "AVnGv0IRBhBGetUGtYJyOU19pSRGxus7gBHYhBktiTWXikj77IA1GfMwQzM7xo9a";
    private static final int timeout = 10_000;

    // GET
    // https://api.jawg.io/routing/route/v1/walk/37.696012,55.620324;37.705061,55.623574?access-token=AVnGv0IRBhBGetUGtYJyOU19pSRGxus7gBHYhBktiTWXikj77IA1GfMwQzM7xo9a&overview=false}
    public Mono<String> getRoute(String coordinates) {

        String url = String.format(
                "https://api.jawg.io/routing/route/v1/walk/%1$s?access-token=%2$s&overview=false", coordinates, token);

        return webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(timeout))
                .onErrorMap(WebClientException::new)
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1000)));
    }
}
