package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.elistratov.model.dto.Slopes;

public interface JsonToSlopesMapper {
    Slopes getSlopesFromJson(String jsonStr) throws JsonProcessingException;
}
