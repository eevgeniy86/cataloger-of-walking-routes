package ru.otus.model.service;

import java.util.List;
import ru.otus.model.domain.Relation;

public interface DBServiceRelation {

    Relation saveRelation(Relation relation);

    List<Relation> saveAll(List<Relation> relations);

    Relation getRelation(Long id);

    List<Relation> getRelationsByRouteId(Long routeId);
}
