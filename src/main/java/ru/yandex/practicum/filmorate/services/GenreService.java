package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;

/**
 * @author Stanislav Makarov
 */
@Service
@AllArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Film updateGenreText(Film film) {
        film.getGenres().forEach(genre -> genre.setName(genreStorage.getGenreText(genre.getId())));
        return film;
    }

    public List<Film> updateGenreText(List<Film> filmList) {
        filmList.forEach(this::updateGenreText);
        return filmList;
    }
}
