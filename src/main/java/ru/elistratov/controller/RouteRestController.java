package ru.elistratov.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import ru.elistratov.converter.RouteUrlConverter;
import ru.elistratov.converter.UrlConverter;
import ru.elistratov.exception.IncorrectUrlException;
import ru.elistratov.exception.RouteNotFoundException;
import ru.elistratov.model.domain.Route;
import ru.elistratov.service.DBServiceRoute;

@RestController
@RequestMapping("${rest.api.prefix}${rest.api.version}")
public class RouteRestController {
    private final UrlConverter<Route> urlConverter = new RouteUrlConverter();
    private final DBServiceRoute dbServiceRoute;

    public RouteRestController(DBServiceRoute dbServiceRoute) {
        this.dbServiceRoute = dbServiceRoute;
    }

    @Operation(summary = "Get route url by id")
    @GetMapping("/route/{id}/url")
    public String getRouteUrlById(@PathVariable(name = "id") long id) {
        var result = dbServiceRoute.getRoute(id);
        var route = result.orElseThrow(() -> new RouteNotFoundException("Route not found"));
        return urlConverter.fromObjectToUrl(route).toString();
    }

    @Operation(summary = "Get route by id")
    @GetMapping("/route/{id}")
    public Route getRouteById(@PathVariable(name = "id") long id) {
        var result = dbServiceRoute.getRoute(id);
        return result.orElseThrow(() -> new RouteNotFoundException("Route not found"));
    }

    @Operation(summary = "Create/update route")
    @PostMapping("/route")
    public Route saveRoute(@RequestBody Route route) {
        return dbServiceRoute.saveRoute(route);
    }

    @Operation(summary = "Filter routes by params")
    @GetMapping("/route")
    public List<Route> getFilteredRoutes(
            @RequestParam(name = "min-length", required = false) Float minLength,
            @RequestParam(name = "max-length", required = false) Float maxLength,
            @RequestParam(name = "min-ascent", required = false) Float minAscent,
            @RequestParam(name = "max-ascent", required = false) Float maxAscent,
            @RequestParam(name = "min-descent", required = false) Float minDescent,
            @RequestParam(name = "max-descent", required = false) Float maxDescent) {
        return dbServiceRoute.getFilteredRoutes(minLength, maxLength, minAscent, maxAscent, minDescent, maxDescent);
    }

    @Operation(summary = "Create route by url")
    @PostMapping("/route/url")
    public Route saveRouteFromUrl(@RequestBody String strUrl) {
        try {
            var uri = new URI(strUrl);
            var toSave = urlConverter.fromUrlToObject(uri.toURL());
            return dbServiceRoute.saveRoute(toSave);
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IncorrectUrlException(e);
        }
    }
}
