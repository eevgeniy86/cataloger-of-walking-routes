package ru.elistratov.supplier.jsommapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;

@Component
public class OsrmJsonMapper {

    public float getDistanceFromJson(String jsonStr) throws JsonProcessingException {
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        return (float) node.get("routes").get(0).get("distance").asDouble();
    }
}
