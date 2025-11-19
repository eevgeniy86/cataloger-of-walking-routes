package ru.elistratov.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.elistratov.model.domain.Relation;
import ru.elistratov.service.DBServiceRelation;

@RestController
@RequestMapping("${rest.api.prefix}${rest.api.version}")
@AllArgsConstructor
@Validated
public class RelationRestController {

    private final DBServiceRelation dbServiceRelation;

    @Operation(summary = "Get relations by route_id")
    @GetMapping("/route/{id}/relation")
    public List<Relation> getRelationsByRouteId(@PathVariable(name = "id") @Min(0) long routeId) {
        return dbServiceRelation.getRelationsByRouteId(routeId);
    }
}
