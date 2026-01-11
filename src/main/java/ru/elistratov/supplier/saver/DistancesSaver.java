package ru.elistratov.supplier;

import ru.elistratov.model.domain.Route;
import java.util.Deque;

public interface DistancesSaver {
    void saveResultToRoute(Deque<Float> distances, Route route);
}
