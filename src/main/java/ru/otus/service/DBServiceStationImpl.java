package ru.otus.service;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.repository.StationRepository;

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
        return ids;
    }
}
