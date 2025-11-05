package ru.otus.repository;

import jakarta.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.otus.model.domain.*;

public class RelationMapper implements RowMapper<Relation> {
    @Nonnull
    @Override
    public Relation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Station station;
        if (rs.getString("station_type") != null) {
            Point point =
                    new Point(rs.getLong("point_id"), rs.getDouble("point_latitude"), rs.getDouble("point_longitude"));
            StationType stationType = StationType.valueOf(rs.getString("station_type"));
            station =
                    new Station(rs.getLong("station_osm_id"), rs.getString("station_name"), stationType, point, false);
        } else {
            station = null;
        }
        RelationType relationType = RelationType.valueOf(rs.getString("type"));
        return new Relation(rs.getLong("id"), rs.getLong("route_id"), relationType, rs.getFloat("distance"), station);
    }
}
