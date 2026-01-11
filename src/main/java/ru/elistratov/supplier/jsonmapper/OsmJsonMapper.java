package ru.elistratov.supplier.jsommapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.Station;
import ru.elistratov.model.domain.StationType;

@Component
public class OsmJsonMapper implements JsonToStationsMapper {

    private static final String busStopKey = "highway";
    private static final String busStopValue = "bus_stop";
    private static final String railStopKey = "railway";
    private static final String railStopValue = "station";
    private static final String subwayKey = "subway";
    private static final String trainKey = "train";

    public List<Station> getStationsFromJson(String jsonStr) throws JsonProcessingException {
        List<Station> result = new ArrayList<>();
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        for (Iterator<JsonNode> it = node.get("elements").elements(); it.hasNext(); ) {
            JsonNode element = it.next();
            Long id = element.get("id").asLong();
            double latitude = element.get("lat").asDouble();
            double longitude = element.get("lon").asDouble();
            JsonNode tags = element.get("tags");
            String name = tags.get("name").asText();
            StationType stationType = null;
            if (tags.has(busStopKey) && tags.get(busStopKey).asText().equals(busStopValue)) {
                stationType = StationType.BUS;
            } else if (tags.has(railStopKey) && tags.get(railStopKey).asText().equals(railStopValue)) {
                if (tags.has(subwayKey) && tags.get(subwayKey).asBoolean()) {
                    stationType = StationType.SUBWAY;
                } else if (tags.has(trainKey) && tags.get(trainKey).asBoolean()) {
                    stationType = StationType.TRAIN;
                }
            }
            if (stationType != null) {
                result.add(new Station(id, name, stationType, new Point(null, latitude, longitude)));
            }
        }
        System.out.println(result);
        return result;
    }
}
