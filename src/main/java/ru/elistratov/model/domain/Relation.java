package ru.elistratov.model.domain;

import jakarta.annotation.Nonnull;
import java.util.Objects;
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
        return "{id=" + id + ";routeId=" + routeId + ";type=" + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return routeId == relation.routeId && Objects.equals(station, relation.station) && type == relation.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, type, station);
    }
}
