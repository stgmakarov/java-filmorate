package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Stanislav Makarov
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Genre {
    @NonNull
    private final int id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;

        Genre genre = (Genre) o;

        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
