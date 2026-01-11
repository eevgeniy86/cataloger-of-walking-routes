package ru.elistratov.service;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.model.domain.Relation;
import ru.elistratov.repository.RelationRepository;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceRelationImpl implements DBServiceRelation {
    private final RelationRepository relationRepository;

    @Override
    @Transactional
    public Relation saveRelationWithExistingStation(Relation relation) {
        Relation savedRelation = relationRepository.saveByParams(
                relation.routeId(),
                relation.type(),
                relation.station() == null ? null : relation.station().getId(),
                relation.distance());
        log.atInfo()
                .setMessage("Saved relation by params: {}")
                .addArgument(savedRelation)
                .log();
        return savedRelation;
    }

    @Override
    public List<Relation> getRelationsByRouteId(Long routeId) {
        var result = Lists.newArrayList(relationRepository.getByRouteId(routeId));
        log.atInfo()
                .setMessage("Get relation by route id: {}")
                .addArgument(routeId)
                .log();
        return result;
    }

    @Override
    public List<Relation> getAllRelations() {
        var result = Lists.newArrayList(relationRepository.findAll());
        log.atInfo().setMessage("Get all relations").log();
        return result;
    }
}
