package ru.elistratov.db.repository;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.elistratov.model.domain.Waypoint;

@Repository
@AllArgsConstructor
public class WaypointRepositoryImpl implements WaypointRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int[] updateDistancesToAll(List<Waypoint> waypoints) {
        String updateDistanceSqlString = "Update Waypoint set distance_to_next = :distanceToNext where id = :id";
        return this.jdbcTemplate.batchUpdate(updateDistanceSqlString, SqlParameterSourceUtils.createBatch(waypoints));
    }
}
