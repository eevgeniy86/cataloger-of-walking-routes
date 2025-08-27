package ru.otus.model.domain;

import jakarta.annotation.Nonnull;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("route")
public record Route(
        @Id @Column("id") Long id,
        String name,
        String description,
        short waypointsNumber,
        @Nonnull @MappedCollection(idColumn = "route_id") Set<Waypoint> waypointsList,
        Float length,
        Float ascent,
        Float descent,
        RelationsProcessingStatus relationsProcessingStatus) {
    @Override
    public String toString() {
        return "{id=" + id + ";name=" + name + ";waypointsNumber=" + waypointsNumber + "}";
    }
}
