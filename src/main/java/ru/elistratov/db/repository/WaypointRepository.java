package ru.elistratov.db.repository;

import java.util.List;
import org.springframework.data.repository.Repository;
import ru.elistratov.model.domain.Waypoint;

public interface WaypointRepository extends Repository<Waypoint, Long> {

    int[] updateDistancesToAll(List<Waypoint> waypoints);
}
