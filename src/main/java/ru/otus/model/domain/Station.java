package ru.otus.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("station")
@AllArgsConstructor
public class Station implements Persistable<Long> {
    @Id
    @Nonnull
    @Column("osm_id")
    @Getter
    private final Long osmId;

    @Getter
    private final String name;

    @Getter
    @Nonnull
    private final StationType type;

    @Getter
    @MappedCollection(idColumn = "station_id")
    @Nonnull
    private final Point point;

    @Transient
    private final boolean isNew;

    @PersistenceCreator
    public Station(Long osmId, String name, StationType type, Point point) {
        this(osmId, name, type, point, false);
    }

    @JsonIgnore
    @Nonnull
    @Override
    public Long getId() {
        return this.osmId;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @Override
    public String toString() {
        return "{id=" + osmId + ";type=" + type + ";name=" + name + "}";
    }
}
