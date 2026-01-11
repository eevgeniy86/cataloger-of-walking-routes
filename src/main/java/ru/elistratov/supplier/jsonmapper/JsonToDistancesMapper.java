package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Deque;

public interface JsonToDistancesMapper {
    Deque<Float> getDistancesFromJson(String jsonStr) throws JsonProcessingException;
}
