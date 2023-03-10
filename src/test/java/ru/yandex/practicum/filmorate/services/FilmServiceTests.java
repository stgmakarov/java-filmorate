package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */
public class FilmServiceTests {
    private FilmService filmService;

    @BeforeEach
    public void initFilmService() {
        filmService = new FilmService();
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
            for (int userId = 1; userId <= filmId; userId++) {
                assertTrue(filmService.like(filmId, userId));
            }
        }

        List<Integer> filmTopList = filmService.getTop(10);

        assertArrayEquals(new int[]{20, 19, 18, 17, 16, 15, 14, 13, 12, 11},
                filmTopList.stream().mapToInt(Integer::intValue).toArray());
    }
}
