package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * @author Stanislav Makarov
 */
public interface FilmStorage {
    int MAX_DESC_LENGTH = 200;
    LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Film create(ru.yandex.practicum.filmorate.model.Film film);

    Film update(Film film);

    List<Film> getListOfFilms();

    Film getFilmById(int filmId);

    void checker(Film film, boolean updateFlag);

    List<Film> getListOfFilms(List<Integer> films);

    boolean like(int filmId, int userId);

    boolean dislike(int filmId, int userId);

    Set<Integer> getLikedUsers(int filmId);

    Set<Genre> getFilmGenres(int filmId);
}
