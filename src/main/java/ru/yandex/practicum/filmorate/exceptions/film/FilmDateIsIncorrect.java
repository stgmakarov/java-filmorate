package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDateIsIncorrect extends ResponseStatusException {
    public FilmDateIsIncorrect(String firstFilmDate) {
        super(HttpStatus.BAD_REQUEST, String.format("Дата не может быть раньше %s", firstFilmDate));
    }
}
