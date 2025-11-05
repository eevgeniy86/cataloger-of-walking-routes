package ru.otus.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.domain.*;

public interface RelationSaveRepository extends Repository<Relation, Long> {

    Relation save(Relation relation);

    @Query("insert into relation (route_id, type, station_id, distance) "
            + "values (:route_id, :type, :station_id, :distance) "
            + "returning id, route_id, type, station_id, distance")
    Relation saveByParamsWithoutInheritors(
            @Param("route_id") Long routeId,
            @Param("type") RelationType type,
            @Param("station_id") Long stationId,
            @Param("distance") Float distance);
}
