package ru.elistratov.service;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.model.domain.Relation;
import ru.elistratov.repository.RelationGetRepository;
import ru.elistratov.repository.RelationSaveRepository;
import ru.elistratov.repository.StationRepository;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceRelationImpl implements DBServiceRelation {
    private final RelationSaveRepository relationSaveRepository;
    private final RelationGetRepository relationGetRepository;
    private final StationRepository stationRepository;

    /*
    Doing so is necessary because isNew-strategies defined to root entity, are applied to related entities.
    In my case Station needs different strategy (PersistableIsNewStrategy) than Relation (getFallbackIsNewStrategy)
     */

    @Override
    @Transactional
    public Relation saveRelation(Relation relation) {
        Relation savedRelation;
        if (relation.station() != null) {
            var savedStation = stationRepository.save(relation.station());
            savedRelation = relationSaveRepository.saveByParamsWithoutInheritors(
                    relation.routeId(), relation.type(), savedStation.getId(), relation.distance());
        } else {
            savedRelation = relationSaveRepository.save(relation);
        }
        log.atInfo().setMessage("Saved relation: {}").addArgument(savedRelation).log();
        return savedRelation;
    }

    @Override
    @Transactional
    public List<Relation> getRelationsByRouteId(Long routeId) {
        var result = Lists.newArrayList(relationGetRepository.getByRouteId(routeId));
        log.atInfo()
                .setMessage("Get relation by route id: {}")
                .addArgument(result)
                .log();
        return result;
    }
}
