package ru.elistratov.supplier.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Station;

public interface StationsGetter {
    List<Station> getStationsToPoint(Point point) throws WebClientException, JsonProcessingException;
}
