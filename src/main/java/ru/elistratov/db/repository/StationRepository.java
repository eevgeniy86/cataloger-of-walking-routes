package ru.elistratov.db.repository;

import java.util.Set;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.elistratov.model.domain.Station;

public interface StationRepository extends Repository<Station, Long> {
    @Query("select s.osm_id as id from station s")
    Set<Long> getAllIds();

    Station save(Station station);
}
