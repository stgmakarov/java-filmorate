package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmIdNotExists extends RuntimeException {
    public FilmIdNotExists(int id){
        log.error(String.format("Фильма с ID %d не существует", id));
    }

    public FilmIdNotExists(String id){
        log.error(String.format("Фильма с ID %s не существует", id));
    }
}
