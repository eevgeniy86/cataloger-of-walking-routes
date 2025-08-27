package ru.otus.model.domain;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("station")
@Getter
public class Station implements Persistable<Long> {
    @Id
    @Nonnull
    private final Long id;

    private final String name;

    private final String network;

    private final StationType type;

    @Transient
    private final boolean isNew;

    public Station(Long id, String name, String network, StationType type, boolean isNew) {
        this.id = id;
        this.name = name;
        this.network = network;
        this.type = type;
        this.isNew = isNew;
    }

    @PersistenceCreator
    public Station(Long id, String name, String network, StationType type) {
        this(id, name, network, type, false);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{id=" + id + ";isNew=" + isNew + ";type=" + type + ";name=" + name + "}";
    }
}
