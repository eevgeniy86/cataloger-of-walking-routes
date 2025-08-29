package ru.otus.controllers;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.domain.Relation;
import ru.otus.service.DBServiceRelation;

@RestController
@RequestMapping("${rest.api.prefix}${rest.api.version}")
@AllArgsConstructor
public class RelationRestController {

    private final DBServiceRelation dbServiceRelation;

    @Operation(summary = "Get relations by route_id")
    @GetMapping("/route/{id}/relation")
    public List<Relation> getRelationsByRouteId(@PathVariable(name = "id") long route_id) {
        return dbServiceRelation.getRelationsByRouteId(route_id);
    }
}
