package ru.elistratov.model.domain;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("waypoint")
public record Waypoint(
        @Id @Column("id") Long id,
        short index,
        String comment,
        @Nonnull @MappedCollection(idColumn = "waypoint_id") Point point) {
    @Override
    public String toString() {
        return "{id=" + id + ";index=" + index + ";point=" + point + "}";
    }
}
