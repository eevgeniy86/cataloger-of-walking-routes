package ru.otus.model.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.model.domain.RelationsProcessingStatus;
import ru.otus.model.domain.Route;

@Repository
public interface RouteRepository extends ReactiveCrudRepository<Route, Long> {

    Mono<Route> save(Route route);

    Mono<Route> findById(Long id);

    Flux<Route> findAllById(Iterable<Long> ids);

    @Query(
            "select * from route r where "
                    + "case when :minLength is null then true when r.length is null then false else r.length >= :minLength end AND "
                    + "case when :maxLength is null then true when r.length is null then false else r.length <= :maxLength end AND "
                    + "case when :minAscent is null then true when r.ascent is null then false else r.ascent >= :minAscent end AND "
                    + "case when :maxAscent is null then true when r.ascent is null then false else r.ascent <= :maxAscent end AND "
                    + "case when :minDescent is null then true when r.descent is null then false else r.descent >= :minDescent end AND "
                    + "case when :maxDescent is null then true when r.descent is null then false else r.descent <= :maxDescent end")
    Flux<Route> filter(
            @Param("minLength") Float minLength,
            @Param("maxLength") Float maxLength,
            @Param("minAscent") Float minAscent,
            @Param("maxAscent") Float maxAscent,
            @Param("minDescent") Float minDescent,
            @Param("maxDescent") Float maxDescent);

    @Query("select * from route r where r.ascent is null")
    Flux<Route> filterWithNoElevations();

    @Query("select * from route r where r.length is null")
    Flux<Route> filterWithNoDistance();

    @Query("select * from route r where lower(r.relations_processing_status) = lower(:#{#status?.name()})")
    Flux<Route> filterByRelationsProcessingStatus(@Param("relationsProcessingStatus") RelationsProcessingStatus status);
}
