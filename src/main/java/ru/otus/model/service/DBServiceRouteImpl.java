package ru.otus.model.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.model.domain.Route;
import ru.otus.model.repository.RouteRepository;

@Service
public class DBServiceRouteImpl implements DBServiceRoute {
    private final RouteRepository routeRepository;
    private static final Logger logger = LoggerFactory.getLogger(DBServiceRouteImpl.class);
    private DBServiceRouteImpl(RouteRepository repository) {
        this.routeRepository = repository;
    }

    @Override
    public Route saveRoute(Route route) {
        var saved = routeRepository.save(route);
        logger.atInfo().setMessage("Saved client: {}").addArgument(saved).log();
        return saved;
    }

    @Override
    public Optional<Route> getRoute(long id) {
        var result = routeRepository.findById(id);
        logger.atInfo().setMessage("Get client by id: {}").addArgument(result).log();
        return result;
    }
}
