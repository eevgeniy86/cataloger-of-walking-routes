package ru.elistratov.service;

import java.util.Set;
import ru.elistratov.model.domain.Station;

public interface DBServiceStation {

    Set<Long> getAllIds();

    void saveStation(Station station);
}
