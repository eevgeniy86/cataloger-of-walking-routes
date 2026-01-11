package ru.elistratov.db.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.db.repository.RouteRepository;
import ru.elistratov.model.domain.RelationsProcessingStatus;
import ru.elistratov.model.domain.Route;

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
    @Transactional
    public Route updateRouteWithStatus(long routeId, RelationsProcessingStatus status) {
        var updated = routeRepository.updateStatus(routeId, status);
        logger.atInfo()
                .setMessage("Updated route: {}, with status: {}")
                .addArgument(updated)
                .addArgument(status)
                .log();
        return updated;
    }

    @Override
    @Transactional
    public Route updateRouteWithSlopes(long routeId, float ascent, float descent) {
        var updated = routeRepository.updateSlopes(routeId, ascent, descent);
        logger.atInfo()
                .setMessage("Updated route: {}, with slopes: {}")
                .addArgument(updated)
                .addArgument(ascent + ", " + descent)
                .log();
        return updated;
    }

    @Override
    public Optional<Route> getRoute(long id) {
        var result = routeRepository.findById(id);
        logger.atInfo().setMessage("Get route by id: {}").addArgument(result).log();
        return result;
    }

    @Override
    public List<Route> getFilteredRoutes(
            Float minLength, Float maxLength, Float minAscent, Float maxAscent, Float minDescent, Float maxDescent) {
        var result = Lists.newArrayList(
                routeRepository.filter(minLength, maxLength, minAscent, maxAscent, minDescent, maxDescent));
        logger.atInfo().setMessage("Get routes by filter").log();
        return result;
    }

    @Override
    public List<Route> getRoutesWithNoDistance() {
        var result = Lists.newArrayList(routeRepository.getWithNoDistance());
        logger.atInfo().setMessage("Get routes with no distance").log();
        return result;
    }

    @Override
    public List<Route> getRoutesByRelationsProcessingStatus(RelationsProcessingStatus status) {
        var result = Lists.newArrayList(routeRepository.getByRelationsProcessingStatus(status.name()));
        logger.atInfo()
                .setMessage("Get routes by relations processing status: {}")
                .addArgument(status)
                .log();
        return result;
    }

    @Override
    public List<Route> getRoutesWithoutElevations() {
        var result = Lists.newArrayList(routeRepository.getWithNoElevations());
        logger.atInfo().setMessage("Get routes with no elevations").log();
        return result;
    }

    @Override
    @Transactional
    public Route updateRouteWithLength(long routeId, float length) {
        var updated = routeRepository.updateLength(routeId, length);
        logger.atInfo()
                .setMessage("Updated route: {}, with distance: {}")
                .addArgument(updated)
                .addArgument(length)
                .log();
        return updated;
    }
}
