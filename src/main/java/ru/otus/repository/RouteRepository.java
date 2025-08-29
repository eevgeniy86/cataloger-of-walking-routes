package ru.otus.repository;

import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.domain.Route;

public interface RouteRepository extends Repository<Route, Long> {

    Route save(Route route);

    Optional<Route> findById(Long id);

    Iterable<Route> findAllById(Iterable<Long> ids);

    @Query(
            "select * from route r where "
                    + "case when :minLength is null then true when r.length is null then false else r.length >= :minLength end AND "
                    + "case when :maxLength is null then true when r.length is null then false else r.length <= :maxLength end AND "
                    + "case when :minAscent is null then true when r.ascent is null then false else r.ascent >= :minAscent end AND "
                    + "case when :maxAscent is null then true when r.ascent is null then false else r.ascent <= :maxAscent end AND "
                    + "case when :minDescent is null then true when r.descent is null then false else r.descent >= :minDescent end AND "
                    + "case when :maxDescent is null then true when r.descent is null then false else r.descent <= :maxDescent end")
    Iterable<Route> filter(
            @Param("minLength") Float minLength,
            @Param("maxLength") Float maxLength,
            @Param("minAscent") Float minAscent,
            @Param("maxAscent") Float maxAscent,
            @Param("minDescent") Float minDescent,
            @Param("maxDescent") Float maxDescent);

    @Query("select * from route r where r.ascent is null")
    Iterable<Route> filterWithNoElevations();

    @Query("select * from route r where r.length is null")
    Iterable<Route> filterWithNoDistance();

    @Query("select * from route r where lower(r.relations_processing_status) = lower(:status)")
    Iterable<Route> filterByRelationsProcessingStatus(@Param("status") String status);
}
