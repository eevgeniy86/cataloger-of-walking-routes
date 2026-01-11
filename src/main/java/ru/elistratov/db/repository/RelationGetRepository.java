package ru.elistratov.db.repository;

import ru.elistratov.model.domain.Relation;

public interface RelationGetRepository {
    Iterable<Relation> getByRouteId(long routeId);
}
