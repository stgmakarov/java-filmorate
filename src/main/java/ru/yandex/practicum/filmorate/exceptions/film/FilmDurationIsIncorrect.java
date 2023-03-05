package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDurationIsIncorrect extends RuntimeException {
    public FilmDurationIsIncorrect(){
        log.error("Продолжительность фильма должна быть положительной");
    }
}
