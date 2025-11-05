package ru.otus.service;

import java.util.List;
import ru.otus.model.domain.Relation;

public interface DBServiceRelation {

    Relation saveRelation(Relation relation);

    List<Relation> getRelationsByRouteId(Long routeId);
}
