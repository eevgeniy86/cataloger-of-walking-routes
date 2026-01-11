package ru.elistratov.model.domain;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("point")
public record Point(@Id @Column("id") Long id, float latitude, float longitude) {
    @Override
    public String toString() {
        return "{id=" + id + ";lat/long(" + latitude + "," + longitude + ")}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Float.compare(latitude, point.latitude) == 0 && Float.compare(longitude, point.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
