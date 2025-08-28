package ru.otus.model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.model.domain.Route;
import ru.otus.model.repository.RouteRepository;

@Service
public class DBServiceRouteImpl implements DBServiceRoute {
    @Autowired
    private final RouteRepository routeRepository;

    private static final Logger logger = LoggerFactory.getLogger(DBServiceRouteImpl.class);

    private DBServiceRouteImpl(RouteRepository repository) {
        this.routeRepository = repository;
    }

    @Override
    @Transactional
    public Mono<Route> saveRoute(Route route) {
        var saved = routeRepository.save(route);
        logger.atInfo().setMessage("Saved route: {}").addArgument(saved).log();
        return saved;
    }

    @Override
    public Mono<Route> getRoute(long id) {
        var result = routeRepository.findById(id);
        logger.atInfo().setMessage("Get route by id: {}").addArgument(result).log();
        return result;
    }

    public Flux<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent) {
        var result = routeRepository.filter(minLength, maxLength, minAscent, maxAscent, minDescent, maxDescent);
        logger.atInfo().setMessage("Get routes by filter").log();
        return result;
    }

    public Flux<Route> getRoutesWithNoDistance() {
        var result = routeRepository.filterWithNoDistance();
        logger.atInfo().setMessage("Get routes with no distance").log();
        return result;
    }
}
