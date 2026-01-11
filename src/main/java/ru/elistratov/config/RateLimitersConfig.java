package ru.elistratov.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
OSRM - data from documentation on https://www.jawg.io/docs/apidocs/#basic-plan-restrictions
OSM - empirical values
 */
@Configuration
public class RateLimitersConfig {
    public static final Duration TIMEOUT = Duration.ofMinutes(5);
    public static final int OSM_LIMIT = 2;
    public static final Duration OSM_REFRESH_PERIOD = Duration.ofSeconds(3);

    public static final int OSRM_LIMIT = 1;
    public static final Duration OSRM_REFRESH_PERIOD = Duration.ofSeconds(1);

    @Bean
    public RateLimiter osmRateLimiter() {

        return RateLimiter.of(
                "osm-rate-limiter",
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(OSM_REFRESH_PERIOD)
                        .limitForPeriod(OSM_LIMIT)
                        .timeoutDuration(TIMEOUT)
                        .build());
    }

    @Bean
    public RateLimiter osrmRateLimiter() {

        return RateLimiter.of(
                "osrm-rate-limiter",
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(OSRM_REFRESH_PERIOD)
                        .limitForPeriod(OSRM_LIMIT)
                        .timeoutDuration(TIMEOUT)
                        .build());
    }
}
