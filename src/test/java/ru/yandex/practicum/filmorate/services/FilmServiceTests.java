package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */
public class FilmServiceTests {
    private FilmService filmService;
    private FilmStorage filmStorage;
    private Film film;

    @BeforeEach
    public void initFilmService() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        film = new Film(0, "Test", "testdesc",
                LocalDate.of(1983, 1, 1),
                180,
                new HashSet<>(),
                new Mpa(1, ""),
                new HashSet<>()

        );
        film.setDuration(120);
        film.setDescription("Alien film");
        film.setReleaseDate(LocalDate.of(1979, 1, 1));
        film = filmStorage.create(film);
    }

    @Test
    public void addLikeTest() {
        assertTrue(filmService.like(1, 1));
        assertTrue(filmService.like(1, 2));
        assertFalse(filmService.like(1, 1));
    }

    @Test
    public void dislikeTest() {
        assertTrue(filmService.like(1, 1));
        assertTrue(filmService.like(1, 2));
        assertFalse(filmService.dislike(1, 3));
        assertTrue(filmService.dislike(1, 2));
    }

    @Test
    public void topTenTest() {
        for (int filmId = 1; filmId <= 20; filmId++) {
            film = new Film(0, "Test", "testdesc",
                    LocalDate.of(1983, 1, 1),
                    180,
                    new HashSet<>(),
                    new Mpa(1, ""),
                    new HashSet<>()

            );
            film.setDuration(120);
            film.setDescription("Alien film");
            film.setReleaseDate(LocalDate.of(1979, 1, 1));
            film = filmStorage.create(film);
            for (int userId = 1; userId <= filmId; userId++) {
                assertTrue(filmService.like(filmId, userId));
            }
        }

        List<Integer> filmTopList = filmService.getTop(10);

        assertArrayEquals(new int[]{20, 19, 18, 17, 16, 15, 14, 13, 12, 11},
                filmTopList.stream().mapToInt(Integer::intValue).toArray());
    }
}
