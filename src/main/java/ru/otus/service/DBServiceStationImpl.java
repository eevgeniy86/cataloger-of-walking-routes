package ru.otus.service;

import java.util.HashMap;
import java.util.Map;
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
    public Map<Long, Long> getAllIds() {
        Map<Long, Long> ids = stationRepository.getAllIds();
        if (ids == null) {
            return new HashMap<>();
        }
        return ids;
    }
}
