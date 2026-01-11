package ru.elistratov.supplier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.model.domain.*;
import ru.elistratov.service.DBServiceRelation;
import ru.elistratov.service.DBServiceRoute;
import ru.elistratov.service.DBServiceStation;

@Component
@Slf4j
public class OsmRelationsSaver implements RelationsSaver {

    private static final int RELATION_TYPES_COUNT = RelationType.values().length;

    private final DBServiceRoute dbRoute;
    private final DBServiceRelation dbRelation;
    private final DBServiceStation dbStation;

    private final Set<Long> existingStationIds;
    private final Set<Relation> existingRelations;
    private final Map<Long, Set<RelationType>> processedRouteIdsRelationsTypes = new ConcurrentSkipListMap<>();

    public OsmRelationsSaver(
            DBServiceRoute dbRoute,
            DBServiceRelation dbRelation,
            DBServiceStation dbStation) {
        this.dbRoute = dbRoute;
        this.dbRelation = dbRelation;
        this.dbStation = dbStation;
        existingStationIds = new ConcurrentSkipListSet<>(dbStation.getAllIds());
        existingRelations = ConcurrentHashMap.newKeySet();
        existingRelations.addAll(dbRelation.getAllRelations());
    }

    @Override
    public void saveResultToRoute(@Nullable List<Station> stations, RelationType type, long routeId) {
        if (type.equals(RelationType.START_FINISH)) {
            saveRelationsOfOneTypeForOneRoute(List.of(new Relation(null, routeId, type, null, null)));
        } else if (stations != null && !stations.isEmpty()) {
            List<Relation> relations = new ArrayList<>();
            for (Station s : stations) {
                s.setIsNew(!existingStationIds.contains(s.getId()));
                relations.add(new Relation(null, routeId, type, null, s));
            }
            saveRelationsOfOneTypeForOneRoute(relations);
        }
        putRelationTypeForRouteId(routeId, type);
    }

    @Transactional
    private void saveRelationsOfOneTypeForOneRoute(List<Relation> relations) {
        for (Relation relation : relations) {
            Station station = relation.station();
            if (station != null && existingStationIds.add(station.getId())) {
                dbStation.saveStation(station);
                log.atInfo()
                        .setMessage("Saved new station: {} for relation: {}")
                        .addArgument(station)
                        .addArgument(relation)
                        .log();
            }
            if (!existingRelations.contains(relation)) {
                dbRelation.saveRelationWithExistingStation(relation);
                log.atInfo()
                        .setMessage("Saved new relation: {}")
                        .addArgument(relation)
                        .log();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void putRelationTypeForRouteId(long routeId, RelationType type) {
        Set<RelationType> relationTypes;
        if (!processedRouteIdsRelationsTypes.containsKey(routeId)) {
            relationTypes = new ConcurrentSkipListSet<>();
            relationTypes.add(type);
            processedRouteIdsRelationsTypes.put(routeId, relationTypes);
        } else {
            relationTypes = processedRouteIdsRelationsTypes.get(routeId);
            relationTypes.add(type);
        }
        if (relationTypes.size() == RELATION_TYPES_COUNT) {
            Route saved = dbRoute.updateRouteWithStatus(routeId, RelationsProcessingStatus.STATIONS_RECEIVED);
            log.atInfo()
                    .setMessage("Relations processed for route: {}")
                    .addArgument(saved)
                    .log();
        }
    }
}
