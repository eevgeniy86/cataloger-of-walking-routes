package ru.elistratov.db.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.elistratov.model.domain.*;

public interface RelationRepository extends Repository<Relation, Long>, RelationGetRepository {

    @Query("insert into relation (route_id, type, station_id, distance) "
            + "values (:#{#relation.routeId}, :#{#relation.type.name}, :#{#relation.station == null ? null : #relation.station.getId}, :#{#relation.distance}) "
            + "returning id, route_id, type, station_id, distance")
    Relation saveWithoutNested(Relation relation);

    Iterable<Relation> findAll();
}
