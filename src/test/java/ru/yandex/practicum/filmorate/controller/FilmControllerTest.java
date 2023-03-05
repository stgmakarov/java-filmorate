package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.film.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */
class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void initFilmController() {
        filmController = new FilmController();
    }
    @Test
    void createFilmOk() {
        Film film = new Film(0,"Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2020,1,1));
        filmController.create(film);

        assertEquals(film.getId(), 1);

        assertEquals(filmController.getAllFilms().get(0).getName(), film.getName());
        assertEquals(filmController.getFilm("1").getName(), film.getName());
    }

}