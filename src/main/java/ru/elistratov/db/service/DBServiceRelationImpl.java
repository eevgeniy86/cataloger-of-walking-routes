package ru.elistratov.db.service;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elistratov.db.repository.RelationRepository;
import ru.elistratov.model.domain.Relation;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceRelationImpl implements DBServiceRelation {
    private final RelationRepository relationRepository;

    @Override
    @Transactional
    public Relation saveRelationWithExistingStation(Relation relation) {
        Relation savedRelation = relationRepository.saveWithoutNested(relation);
        log.atInfo()
                .setMessage("Saved relation by params: {}")
                .addArgument(savedRelation)
                .log();
        return savedRelation;
    }

    @Override
    public List<Relation> getRelationsByRouteId(long routeId) {
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
