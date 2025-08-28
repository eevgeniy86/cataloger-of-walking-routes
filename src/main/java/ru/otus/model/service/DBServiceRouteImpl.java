package ru.otus.model.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Route saveRoute(Route route) {
        var saved = routeRepository.save(route);
        logger.atInfo().setMessage("Saved route: {}").addArgument(saved).log();
        return saved;
    }

    @Override
    public Optional<Route> getRoute(long id) {
        var result = routeRepository.findById(id);
        logger.atInfo().setMessage("Get route by id: {}").addArgument(result).log();
        return result;
    }

    public List<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent) {
        var result = Lists.newArrayList(
                routeRepository.filter(minLength, maxLength, minAscent, maxAscent, minDescent, maxDescent));
        logger.atInfo().setMessage("Get routes by filter").log();
        return result;
    }

    public List<Route> getRoutesWithNoDistance() {
        var result = Lists.newArrayList(routeRepository.filterWithNoDistance());
        logger.atInfo().setMessage("Get routes with no distance").log();
        return result;
    }
}
