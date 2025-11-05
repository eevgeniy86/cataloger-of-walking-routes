package ru.otus.repository;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.model.domain.Relation;

/*
Different repository for select for the reason of getting consistent relation-objects with all inheritors
TODO: Custom repository implementations for all entities
 */
@Repository
@AllArgsConstructor
public class RelationGetRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Iterable<Relation> getByRouteId(Long routeId) {
        String query = "select "
                + "r.id as id, r.route_id as route_id, r.type as type, r.distance as distance, "
                + "s.osm_id as station_osm_id, s.name as station_name, s.type as station_type, "
                + "p.id as point_id, p.latitude as point_latitude, p.longitude as point_longitude "
                + "from relation r left join station s on r.station_id = s.osm_id "
                + "left join point p on s.osm_id = p.station_id "
                + "where r.route_id = :routeId";
        Map<String, Object> params = new HashMap<>();
        params.put("routeId", routeId);
        return jdbcTemplate.query(query, params, new RelationMapper());
    }
}
