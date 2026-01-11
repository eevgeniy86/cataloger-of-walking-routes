package ru.elistratov.supplier.jsonmapper;

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

    private static final String BUS_STOP_KEY = "highway";
    private static final String BUS_STOP_VALUE = "bus_stop";
    private static final String RAIL_STOP_KEY = "railway";
    private static final String RAIL_STOP_VALUE = "station";
    private static final String SUBWAY_KEY = "subway";
    private static final String TRAIN_KEY = "train";

    public List<Station> getStationsFromJson(String jsonStr) throws JsonProcessingException {
        List<Station> result = new ArrayList<>();
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode node = mapper.readTree(jsonStr);
        for (Iterator<JsonNode> it = node.get("elements").elements(); it.hasNext(); ) {
            JsonNode element = it.next();
            Long id = element.get("id").asLong();
            float latitude = (float) element.get("lat").asDouble();
            float longitude = (float) element.get("lon").asDouble();
            JsonNode tags = element.get("tags");
            String name = tags.get("name").asText();
            StationType stationType = null;
            if (tags.has(BUS_STOP_KEY) && tags.get(BUS_STOP_KEY).asText().equals(BUS_STOP_VALUE)) {
                stationType = StationType.BUS;
            } else if (tags.has(RAIL_STOP_KEY)
                    && tags.get(RAIL_STOP_KEY).asText().equals(RAIL_STOP_VALUE)) {
                if (tags.has(SUBWAY_KEY) && tags.get(SUBWAY_KEY).asBoolean()) {
                    stationType = StationType.SUBWAY;
                } else if (tags.has(TRAIN_KEY) && tags.get(TRAIN_KEY).asBoolean()) {
                    stationType = StationType.TRAIN;
                }
            }
            if (stationType != null) {
                result.add(new Station(id, name, stationType, new Point(null, latitude, longitude)));
            }
        }
        return result;
    }
}
