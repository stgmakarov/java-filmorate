package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDurationIsIncorrect extends ResponseStatusException {
    public FilmDurationIsIncorrect() {
        super(HttpStatus.BAD_REQUEST, "Продолжительность фильма должна быть положительной");
    }
}
