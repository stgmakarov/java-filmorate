package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stanislav Makarov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Integer> getTop(int topElements) {
        return filmStorage.getListOfFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> getLikesCount(film.getId())).reversed())
                .limit(topElements)
                .map(Film::getId)
                .collect(Collectors.toList());
    }

    public boolean like(int filmId, int userId) {
        return filmStorage.like(filmId, userId);
    }

    public int getLikesCount(int filmId) {
        return filmStorage.getFilmById(filmId).getLikedUsers().size();
    }

    public boolean dislike(int filmId, int userId) {
        return filmStorage.dislike(filmId, userId);
    }
}
