package ru.elistratov.supplier.saver;

import java.util.List;
import org.springframework.lang.Nullable;
import ru.elistratov.model.domain.RelationType;
import ru.elistratov.model.domain.Station;

public interface RelationsSaver {
    void saveResultToRoute(@Nullable List<Station> stations, RelationType type, long routeId);
}
