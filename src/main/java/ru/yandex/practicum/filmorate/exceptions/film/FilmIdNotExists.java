package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmIdNotExists extends ResponseStatusException {
    public FilmIdNotExists(int id) {
        super(HttpStatus.NOT_FOUND, String.format("Фильма с ID %d не существует", id));
    }
}
