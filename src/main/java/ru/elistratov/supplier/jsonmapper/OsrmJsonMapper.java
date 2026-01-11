package ru.elistratov.supplier.jsommapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;
import ru.elistratov.model.dto.Slopes;

@Component
public class OsrmJsonMapper implements JsonToDistanceMapper, JsonToSlopesMapper {

    @Override
    public float getDistanceFromJson(String jsonStr) throws JsonProcessingException {
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        return (float) node.get("routes").get(0).get("distance").asDouble();
    }

    @Override
    public Slopes getSlopesFromJson(String jsonStr) throws JsonProcessingException {
        //        JsonMapper mapper = JsonMapper.builder().build();
        //        JsonNode node = mapper.readTree(jsonStr);
        //        return (float) node.get("routes").get(0).get("distance").asDouble();
        return null;
    }
}
