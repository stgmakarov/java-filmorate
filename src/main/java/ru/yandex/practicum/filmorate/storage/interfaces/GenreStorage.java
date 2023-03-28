package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

/**
 * @author Stanislav Makarov
 */
public interface GenreStorage {
    List<Genre> getGenre();

    Genre getGenre(int id);

    void updateFilmGenres(int filmId, Set<Integer> genres);

    String getGenreText(int id);
}
