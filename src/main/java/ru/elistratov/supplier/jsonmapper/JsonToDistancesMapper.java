package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonToDistanceMapper {
    float getDistanceFromJson(String jsonStr) throws JsonProcessingException;
}
