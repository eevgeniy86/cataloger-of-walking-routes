package ru.elistratov.supplier.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.*;
import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Point;

@Component
public class OsrmJsonMapper implements JsonToDistancesMapper, JsonToElevationsMapper {

    @Override
    public Deque<Float> getDistancesFromJson(String jsonStr) throws JsonProcessingException {
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        LinkedList<Float> distances = new LinkedList<>();
        for (Iterator<JsonNode> it = node.get("routes").get(0).get("legs").elements(); it.hasNext(); ) {
            distances.addLast(it.next().get("distance").floatValue());
        }
        return distances;
    }

    @Override
    public SequencedMap<Point, Float> getElevationsFromJson(String jsonStr) throws JsonProcessingException {
        JsonMapper mapper = JsonMapper.builder().build();
        SequencedMap<Point, Float> elevations = new LinkedHashMap<>();
        for (Iterator<JsonNode> it = mapper.readTree(jsonStr).elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            float latitude = node.get("location").get("lat").floatValue();
            float longitude = node.get("location").get("lng").floatValue();
            float elevation = node.get("elevation").floatValue();
            elevations.put(new Point(null, latitude, longitude), elevation);
        }
        return elevations;
    }
}
