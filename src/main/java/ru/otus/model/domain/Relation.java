package ru.otus.model.domain;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("relation")
public record Relation(
        @Id @Column("id") Long id,
        long routeId,
        @Nonnull RelationType type,
        Float distance,
        @MappedCollection(idColumn = "osm_id") @Column("station_id") Station station) {

    @Override
    public String toString() {
        return "{id=" + id + ";type=" + type + "}";
    }
}
