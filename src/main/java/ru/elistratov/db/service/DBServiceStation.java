package ru.elistratov.db.service;

import java.util.Set;
import ru.elistratov.model.domain.Station;

public interface DBServiceStation {

    Set<Long> getAllIds();

    Station saveStation(Station station);
}
