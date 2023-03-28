package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author Stanislav Makarov
 */
@Data
public class Mpa {
    @NonNull
    private final int id;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mpa)) return false;

        Mpa mpa = (Mpa) o;

        return id == mpa.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
