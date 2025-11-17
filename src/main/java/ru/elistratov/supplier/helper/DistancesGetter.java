package ru.elistratov.supplier.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.elistratov.exception.WebClientException;
import ru.elistratov.model.domain.Route;

public interface DistancesGetter {

    float getDistanceForRoute(Route route) throws WebClientException, JsonProcessingException;
}
