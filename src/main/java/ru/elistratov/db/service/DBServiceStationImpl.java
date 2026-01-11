package ru.elistratov.service;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.elistratov.model.domain.Station;
import ru.elistratov.repository.StationRepository;

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

    public void saveStation(Station station) {
        stationRepository.save(station);
        log.atInfo().setMessage("Saved station: {}").addArgument(station).log();
    }
}
