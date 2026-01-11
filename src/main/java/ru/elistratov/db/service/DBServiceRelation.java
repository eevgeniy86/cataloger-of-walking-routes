package ru.elistratov.db.service;

import java.util.List;
import ru.elistratov.model.domain.Relation;

public interface DBServiceRelation {

    Relation saveRelationWithExistingStation(Relation relation);

    List<Relation> getRelationsByRouteId(long routeId);

    List<Relation> getAllRelations();
}
