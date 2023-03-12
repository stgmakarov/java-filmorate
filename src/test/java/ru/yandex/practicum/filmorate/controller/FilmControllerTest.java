package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.film.FilmDateIsIncorrect;
import ru.yandex.practicum.filmorate.exceptions.film.FilmDescriptionTooMachLength;
import ru.yandex.practicum.filmorate.exceptions.film.FilmDurationIsIncorrect;
import ru.yandex.practicum.filmorate.exceptions.film.FilmIdNotExists;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * @author Stanislav Makarov
 */

class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void initFilmController() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(filmStorage, new FilmService(filmStorage), new InMemoryUserStorage());
    }

    @Test
    void createFilmOk() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        filmController.create(film);

        assertEquals(film.getId(), 1);

        assertEquals(filmController.getAllFilms().get(0).getName(), film.getName());
        assertEquals(filmController.getFilm(1).getName(), film.getName());
    }

    @Test
    void updateFilmOk() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film = filmController.create(film);

        film.setName("Test2");
        film.setDuration(200);

        Film film2 = filmController.update(film);
        assertEquals(film.getName(), film2.getName());
        assertEquals(film.getDuration(), film2.getDuration());
    }

    @Test
    void updateFilmFail() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        filmController.create(film);

        film.setId(9999);

        assertThrowsExactly(FilmIdNotExists.class, () -> filmController.update(film));
    }

    @Test
    void updateFilmFail2() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        filmController.create(film);

        film.setDuration(-100);

        assertThrowsExactly(FilmDurationIsIncorrect.class, () -> filmController.update(film));
    }

    @Test
    void createFilmFailFilmDateIncorrect() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrowsExactly(FilmDateIsIncorrect.class, () -> filmController.create(film));
    }

    @Test
    void createFilmFailFilmDescriptionTooMachLength() {
        Film film = new Film(0, "Test");
        film.setDuration(100);
        film.setDescription("test descriptionassssssssssssssssssssssssssssssssssss" +
                "asssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sdfffffffffffffffffgsdgergerg" +
                "ergerrrrrreeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                "dgrggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrowsExactly(FilmDescriptionTooMachLength.class, () -> filmController.create(film));
    }

    @Test
    void createFilmFailFilmDurationIsIncorrect() {
        Film film = new Film(0, "Test");
        film.setDuration(-5);
        film.setDescription("test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrowsExactly(FilmDurationIsIncorrect.class, () -> filmController.create(film));
    }

}