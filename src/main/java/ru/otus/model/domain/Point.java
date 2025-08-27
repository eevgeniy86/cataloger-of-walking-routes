package ru.otus.model.domain;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("point")
public record Point(@Id @Column("id") Long id, @Nonnull double latitude, @Nonnull double longitude, Double elevation) {
    @Override
    public String toString() {
        return "{id=" + id + ";lat/long(" + latitude + "," + longitude + ")}";
    }
}
