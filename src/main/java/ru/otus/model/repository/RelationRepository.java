package ru.otus.model.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.domain.Relation;
import ru.otus.model.domain.Route;

public interface RelationRepository extends ListCrudRepository<Relation, Long> {

    @Query("select * from relation r where r.route_id = :routeId")
    Iterable<Route> getByRouteId(@Param("routeId") Long routeId);
}
