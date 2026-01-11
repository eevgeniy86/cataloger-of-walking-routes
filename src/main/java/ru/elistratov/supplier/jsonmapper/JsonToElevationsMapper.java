package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.SequencedMap;
import ru.elistratov.model.domain.Point;

public interface JsonToElevationsMapper {
    SequencedMap<Point, Float> getElevationsFromJson(String jsonStr) throws JsonProcessingException;
}
