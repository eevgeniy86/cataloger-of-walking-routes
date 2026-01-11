package ru.elistratov.db.service;

import java.util.List;
import ru.elistratov.model.domain.Waypoint;

public interface DBServiceWaypoint {

    int updateWaypointsWithDistances(List<Waypoint> waypoints);
}
