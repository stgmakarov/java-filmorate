package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDateIsIncorrect extends RuntimeException {
    public FilmDateIsIncorrect(String firstFilmDate){
        log.error(String.format("Дата не может быть раньше %s", firstFilmDate));
    }
}
