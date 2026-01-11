package ru.elistratov.db.service;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.db.repository.StationRepository;
import ru.elistratov.model.domain.Station;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceStationImpl implements DBServiceStation {
    private final StationRepository stationRepository;

    @Override
    public Set<Long> getAllIds() {
        Set<Long> ids = stationRepository.getAllIds();
        if (ids == null) {
            return new HashSet<>();
        }
        log.atInfo().setMessage("Get all stationIds").log();
        return ids;
    }

    @Override
    @Transactional
    public Station saveStation(Station station) {
        Station saved = stationRepository.save(station);
        log.atInfo().setMessage("Saved station: {}").addArgument(saved).log();
        return saved;
    }
}
