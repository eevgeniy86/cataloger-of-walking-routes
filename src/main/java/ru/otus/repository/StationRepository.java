package ru.otus.repository;

import java.util.Map;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.otus.model.domain.Station;

public interface StationRepository extends Repository<Station, Long> {
    @Query("select osm_id, id from station")
    Map<Long, Long> getAllIds();

    Station save(Station station);
}
