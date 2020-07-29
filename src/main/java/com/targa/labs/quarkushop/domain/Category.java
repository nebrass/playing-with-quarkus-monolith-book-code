package com.targa.labs.quarkushop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A Category.
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "categories")
public class Category extends AbstractEntity {

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    public Category(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) &&
                Objects.equals(description, category.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
