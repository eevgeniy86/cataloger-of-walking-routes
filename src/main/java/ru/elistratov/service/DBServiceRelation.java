package ru.elistratov.service;

import java.util.List;
import ru.elistratov.model.domain.Relation;

public interface DBServiceRelation {

    Relation saveRelation(Relation relation);

    List<Relation> getRelationsByRouteId(Long routeId);
}
