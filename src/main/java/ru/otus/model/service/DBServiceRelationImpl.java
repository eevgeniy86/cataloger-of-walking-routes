package ru.otus.model.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.model.domain.Relation;
import ru.otus.model.repository.RelationRepository;

@Service
@Slf4j
@AllArgsConstructor
public class DBServiceRelationImpl implements DBServiceRelation {
    private final RelationRepository relationRepository;

    @Override
    @Transactional
    public Relation saveRelation(Relation relation) {
        var saved = relationRepository.save(relation);
        log.atInfo().setMessage("Saved relation: {}").addArgument(saved).log();
        return saved;
    }

    @Override
    @Transactional
    public List<Relation> saveAll(List<Relation> relations) {
        var saved = relationRepository.saveAll(relations);
        log.atInfo().setMessage("Saved relations: {}").addArgument(saved).log();
        return saved;
    }

    @Override
    public Optional<Relation> getRelation(Long id) {
        var result = relationRepository.findById(id);
        log.atInfo().setMessage("Get relation by id: {}").addArgument(result).log();
        return result;
    }

    @Override
    public List<Relation> getRelationsByRouteId(Long routeId) {
        var result = Lists.newArrayList(relationRepository.getByRouteId(routeId));
        log.atInfo()
                .setMessage("Get relation by route id: {}")
                .addArgument(result)
                .log();
        return result;
    }
}
