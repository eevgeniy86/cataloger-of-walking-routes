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
        @Nonnull @MappedCollection(idColumn = "id") Point firstPoint,
        @Nonnull @MappedCollection(idColumn = "id") Point secondPoint,
        @MappedCollection(idColumn = "id") Station station,
        float distance) {
    @Override
    public String toString() {
        return "{id=" + id + ";type=" + type + ";firstPoint=" + firstPoint + ";secondPoint=" + secondPoint + ";station="
                + station + "}";
    }
}
