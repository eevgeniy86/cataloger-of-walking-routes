package ru.elistratov.supplier.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.converter.PointsToOSMCoordinatesConverter;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Station;
import ru.elistratov.supplier.jsommapper.OsmJsonMapper;
import ru.elistratov.webclient.OsmWebClient;

/*
    Using reduce to mono and block method while handling getPublicTransportStops response,
    instead of working with flux response,
    because Overpass api chunked response contains string parts, not valid json-objects
*/
@Component
@Slf4j
@AllArgsConstructor
public class StationsGetterImpl implements StationsGetter {

    private final OsmWebClient httpClient;
    private final PointsToOSMCoordinatesConverter converter;
    private final OsmJsonMapper osmJsonMapper;

    @Override
    public List<Station> getStationsToPoint(Point point) throws WebClientException, JsonProcessingException {
        String coordinates = converter.convertPointToCoordinates(point);
        List<Station> stations;
        String jsonStr = httpClient
                .getPublicTransportStops(coordinates)
                .reduce("", (a, b) -> a + b)
                .block();
        stations = new ArrayList<>(osmJsonMapper.getStationsFromJson((jsonStr)));
        return stations;
    }
}
