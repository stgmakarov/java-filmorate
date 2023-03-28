package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.film.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Stanislav Makarov
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int filmLastId = 1;
    private final Map<Integer, Film> filmMap = new HashMap<>();

    /**
     * Увеличение ID на тот случай, если будет вызвано одновременно двумя клинтами
     */
    private synchronized int getLastId() {
        return filmLastId++;
    }

    @Override
    public Film create(Film film) {
        checker(film, false);

        int lastId = getLastId();
        film.setId(lastId);
        filmMap.put(lastId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checker(film, true);
        int filmId = film.getId();
        Film oldFilm = filmMap.get(filmId);
        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setLikedUsers(film.getLikedUsers());
        filmMap.put(filmId, oldFilm);
        return oldFilm;
    }

    @Override
    public List<Film> getListOfFilms() {
        return List.copyOf(filmMap.values());
    }

    @Override
    public List<Film> getListOfFilms(List<Integer> films) {
        return filmMap.values().stream()
                .filter(film -> films.contains(film.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!filmMap.containsKey(filmId)) throw new FilmIdNotExists(filmId);
        return filmMap.get(filmId);
    }

    public void checker(Film film, boolean updateFlag) {
        if (updateFlag) {
            int filmId = film.getId();
            if (!filmMap.containsKey(filmId)) throw new FilmIdNotExists(filmId);
        }
        if (film.getName().isEmpty()) throw new EmptyFilmNameException();
        if (film.getDescription().length() > MAX_DESC_LENGTH) throw new FilmDescriptionTooMachLength(MAX_DESC_LENGTH);
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new FilmDateIsIncorrect(FIRST_FILM_DATE.format(formatter));
        if (film.getDuration() <= 0) throw new FilmDurationIsIncorrect();
    }

    @Override
    public boolean like(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (film.getLikedUsers().contains(userId)) {
            log.info("Нельзя лайкать дважды");
            return false;
        }
        film.getLikedUsers().add(userId);
        update(film);
        return true;
    }

    @Override
    public boolean dislike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (!film.getLikedUsers().contains(userId)) {
            log.info("Убрать лайк можно только у понравившихся фильмов");
            return false;
        }
        film.getLikedUsers().remove(userId);
        update(film);
        return true;
    }

    @Override
    public Set<Integer> getLikedUsers(int filmId) {
        return filmMap.get(filmId).getLikedUsers();
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        return filmMap.get(filmId).getGenres();
    }
}
