package ru.elistratov.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.elistratov.model.domain.*;

public interface RelationRepository extends Repository<Relation, Long>, RelationGetRepository {

    /*
    Saving relations and stations
     */
    @Query("insert into relation (route_id, type, station_id, distance) "
            + "values (:route_id, :type, :station_id, :distance) "
            + "returning id, route_id, type, station_id, distance")
    Relation saveByParams(
            @Param("route_id") Long routeId,
            @Param("type") RelationType type,
            @Param("station_id") Long stationId,
            @Param("distance") Float distance);

    Iterable<Relation> findAll();
}
