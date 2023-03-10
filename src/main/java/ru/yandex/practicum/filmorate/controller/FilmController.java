package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.film.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.film.MissedLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private final int TOP_FILMS_COUNT = 10;
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film = filmStorage.create(film);
        filmService.filmInit(film.getId());
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getListOfFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@Valid @PathVariable("id") int id) {
        return filmStorage.getFilmById(id);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@Valid @PathVariable("id") int filmId, @Valid @PathVariable("userId") int userId) {
        filmStorage.getFilmById(filmId);//проверка на существование ИД
        userStorage.getUserById(userId);//проверка на существование ИД
        if(!filmService.like(filmId, userId)) throw new AlreadyLikedException();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void dislike(@Valid @PathVariable("id") int filmId, @Valid @PathVariable("userId") int userId) {
        filmStorage.getFilmById(filmId);//проверка на существование ИД
        userStorage.getUserById(userId);//проверка на существование ИД
        if(!filmService.dislike(filmId, userId))throw new MissedLikeException();
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam Optional<Integer> count) {
        List<Integer> topFilmsId = filmService.getTop(count.orElse(TOP_FILMS_COUNT));
        return filmStorage.getListOfFilms(topFilmsId);
    }
}
