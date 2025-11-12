package ru.elistratov.converter;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.elistratov.exception.IncorrectUrlException;
import ru.elistratov.exception.UrlConstructorException;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.RelationsProcessingStatus;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;

@Component
public class RouteUrlConverter implements UrlConverter<Route> {
    private static final Logger logger = LoggerFactory.getLogger(RouteUrlConverter.class);

    private static final String protocol = "https";
    private static final String host = "yandex.ru";
    private static final String path = "/maps";
    private static final String queryPrefix = "mode=routes";
    private static final String queryWaypointsParamName = "rtext=";
    private static final String querySuffix = "&rtt=pd";
    private static final String queryWaypointsDelimiter = "~";
    private static final String queryCoordinatesDelimiter = ",";
    private static final String queryCoordinatesDelimiterRegexp = ",|%2c";

    @Override
    // https://yandex.ru/maps?mode=routes&rtext=55.620324%2C37.696012~55.623574%2C37.705061&rtt=pd
    public URL fromObjectToUrl(Route route) throws UrlConstructorException {
        String query = route.waypointsList().stream()
                .sorted(Comparator.comparingInt(Waypoint::index))
                .map(w -> w.point().latitude()
                        + queryCoordinatesDelimiter
                        + w.point().longitude())
                .collect(Collectors.joining(
                        queryWaypointsDelimiter, queryPrefix + "&" + queryWaypointsParamName, querySuffix));
        try {
            URI uri = new URI(protocol, host, path, query, null);
            URL url = uri.toURL();
            logger.atInfo()
                    .setMessage("Route: {} converted to url: {}")
                    .addArgument(route)
                    .addArgument(url)
                    .log();
            return url;
        } catch (URISyntaxException | MalformedURLException e) {
            logger.atError()
                    .setMessage("Can't create url for route: {}")
                    .addArgument(route)
                    .log();
            throw new UrlConstructorException(e);
        }
    }

    @Override
    public Route fromUrlToObject(URL url) {
        String[] pointsStringArray = getCoordinatesArray(url);
        try {
            var route = getRouteFromCoordinatesArray(pointsStringArray);
            logger.atInfo()
                    .setMessage("Url: {} parsed to route: {}")
                    .addArgument(url)
                    .addArgument(route)
                    .log();
            return route;
        } catch (Exception e) {
            logger.atError()
                    .setMessage("Exception: {}, while parsing url: {}")
                    .addArgument(e.getMessage())
                    .addArgument(url)
                    .log();
            throw new IncorrectUrlException(e);
        }
    }

    private String[] getCoordinatesArray(URL url) {
        String queryStr = url.getQuery().toLowerCase();
        return queryStr.substring(
                        queryStr.indexOf(queryWaypointsParamName) + queryWaypointsParamName.length(),
                        queryStr.indexOf("&", queryStr.indexOf(queryWaypointsParamName)))
                .split(queryWaypointsDelimiter);
    }

    // pointsStringArray format: [55.620324%2C37.696012, 55.623574%2C37.705061]
    private Route getRouteFromCoordinatesArray(String[] pointsStringArray) {
        Set<Waypoint> waypoints = new HashSet<>();
        short index = 0;
        for (String s : pointsStringArray) {
            String[] coordinatesArray = s.split(queryCoordinatesDelimiterRegexp);
            var latitude = coordinatesArray[0];
            var longitude = coordinatesArray[1];
            waypoints.add(new Waypoint(
                    null, index, null, new Point(null, Float.parseFloat(latitude), Float.parseFloat(longitude))));
            index++;
        }
        return new Route(null, null, null, index, waypoints, null, null, null, RelationsProcessingStatus.NOT_PROCESSED);
    }
}
