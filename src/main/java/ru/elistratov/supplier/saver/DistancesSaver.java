package ru.elistratov.supplier.saver;

import java.util.Deque;
import ru.elistratov.model.domain.Route;

public interface DistancesSaver {
    void saveResultToRoute(Deque<Float> distances, Route route);
}
