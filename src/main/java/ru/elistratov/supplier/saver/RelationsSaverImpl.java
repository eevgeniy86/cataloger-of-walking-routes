package ru.elistratov.supplier.saver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.elistratov.db.service.DBServiceRelation;
import ru.elistratov.db.service.DBServiceRoute;
import ru.elistratov.db.service.DBServiceStation;
import ru.elistratov.model.domain.*;

@Component
@Slf4j
public class RelationsSaverImpl implements RelationsSaver {

    private static final int RELATION_TYPES_COUNT = RelationType.values().length;

    private final DBServiceRoute dbRoute;
    private final DBServiceRelation dbRelation;
    private final DBServiceStation dbStation;

    private final Set<Long> existingStationIds;
    private final Set<Relation> existingRelations;
    private final Map<Long, Set<RelationType>> processedRouteIdsRelationsTypes = new ConcurrentSkipListMap<>();

    public RelationsSaverImpl(DBServiceRoute dbRoute, DBServiceRelation dbRelation, DBServiceStation dbStation) {
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
                if (!existingStationIds.contains(s.getId())) {
                    s.setIsNew(true);
                    Station saved = dbStation.saveStation(s);
                    if (saved != null) {
                        existingStationIds.add(saved.getId());
                        log.atInfo()
                                .setMessage("Saved station: {}")
                                .addArgument(saved)
                                .log();
                    }
                }
                relations.add(new Relation(null, routeId, type, null, s));
            }
            saveRelationsOfOneTypeForOneRoute(relations);
        }
        putRelationTypeForRouteId(routeId, type);
    }

    private void saveRelationsOfOneTypeForOneRoute(List<Relation> relations) {
        for (Relation relation : relations) {
            if (!existingRelations.contains(relation)) {
                Relation saved = dbRelation.saveRelationWithExistingStation(relation);
                if (saved != null) {
                    existingRelations.add(saved);
                    log.atInfo()
                            .setMessage("Saved new relation: {}")
                            .addArgument(saved)
                            .log();
                }
            }
        }
    }

    private void putRelationTypeForRouteId(long routeId, RelationType type) {
        Set<RelationType> relationTypes =
                processedRouteIdsRelationsTypes.computeIfAbsent(routeId, k -> new ConcurrentSkipListSet<>());
        relationTypes.add(type);
        if (relationTypes.size() == RELATION_TYPES_COUNT) {
            Route saved = dbRoute.updateRouteWithStatus(routeId, RelationsProcessingStatus.ADDED);
            log.atInfo()
                    .setMessage("Relations processed for route: {}")
                    .addArgument(saved)
                    .log();
        }
    }
}
