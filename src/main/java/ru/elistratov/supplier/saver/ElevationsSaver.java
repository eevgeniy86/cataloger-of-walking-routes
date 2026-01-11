package ru.elistratov.supplier.saver;

import java.util.SequencedMap;
import ru.elistratov.model.domain.Point;

public interface ElevationsSaver {
    void saveResultToRoute(
            SequencedMap<Point, Float> elevations, short lastWaypointIndex, int seriesCount, long routeId);
}
