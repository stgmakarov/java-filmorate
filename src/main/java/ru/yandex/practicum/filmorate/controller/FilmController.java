package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.film.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.film.MissedLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping(value = "/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final UserStorage userStorage;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film = filmService.create(film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getListOfFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") int id) {
        return filmService.getFilmById(id);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.getFilmById(filmId);//проверка на существование ИД
        userStorage.getUserById(userId);//проверка на существование ИД
        if (!filmService.like(filmId, userId)) throw new AlreadyLikedException();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void dislike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.getFilmById(filmId);//проверка на существование ИД
        userStorage.getUserById(userId);//проверка на существование ИД
        if (!filmService.dislike(filmId, userId)) throw new MissedLikeException();
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        List<Integer> topFilmsId = filmService.getTop(count);
        return filmService.getListOfFilms(topFilmsId);
    }
}
