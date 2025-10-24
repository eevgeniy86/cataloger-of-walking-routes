package ru.otus.model.domain;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("station")
public record Station(
        @Id @Column("id") Long id,
        @Nonnull Long osm_id,
        String name,
        String network,
        StationType type,
        @Nonnull @MappedCollection(idColumn = "id") Point point) {

    @Override
    public String toString() {
        return "{id=" + id + ";type=" + type + ";name=" + name + "}";
    }
}
