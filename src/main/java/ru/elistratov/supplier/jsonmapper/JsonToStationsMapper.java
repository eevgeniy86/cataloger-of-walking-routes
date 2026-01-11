package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import ru.elistratov.model.domain.Station;

public interface JsonToStationsMapper {
    List<Station> getStationsFromJson(String jsonStr) throws JsonProcessingException;
}
