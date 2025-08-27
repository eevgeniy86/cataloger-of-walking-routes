package ru.otus.model.repository;

import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.domain.RelationsProcessingStatus;
import ru.otus.model.domain.Route;

public interface RouteRepository extends Repository<Route, Long> {

    Route save(Route client);

    Optional<Route> findById(Long id);

    Iterable<Route> findAllById(Iterable<Long> ids);

    @Query(
            "select * from route r where r.length between :minLength and :maxLength and r.ascent between :minAscent and :maxAscent and r.descent between :minDescent and :maxDescent")
    Iterable<Route> filter(
            @Param("minLength") float minLength,
            @Param("maxLength") float maxLength,
            @Param("minAscent") float minAscent,
            @Param("maxAscent") float maxAscent,
            @Param("minDescent") float minDescent,
            @Param("maxDescent") float maxDescent);

    @Query("select * from route r where r.ascent is null")
    Iterable<Route> filterWithNotProcessedElevations();

    @Query("select * from route r where r.length is null")
    Iterable<Route> filterWithNotProcessedLength();

    @Query("select * from route r where lower(r.relations_processing_status) = lower(:#{#status?.name()})")
    Iterable<Route> filterByRelationsProcessingStatus(
            @Param("relationsProcessingStatus") RelationsProcessingStatus status);
}
