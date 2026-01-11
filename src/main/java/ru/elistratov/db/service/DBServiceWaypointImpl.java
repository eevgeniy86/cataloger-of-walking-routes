package ru.elistratov.db.service;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.db.repository.WaypointRepository;
import ru.elistratov.model.domain.Waypoint;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceWaypointImpl implements DBServiceWaypoint {
    private final WaypointRepository waypointRepository;

    @Override
    @Transactional
    public int updateWaypointsWithDistances(List<Waypoint> waypoints) {
        int count = Arrays.stream(waypointRepository.updateDistancesToAll(waypoints))
                .sum();
        log.atInfo()
                .setMessage("Updated distances for waypoints count: {}")
                .addArgument(count)
                .log();
        return count;
    }
}
