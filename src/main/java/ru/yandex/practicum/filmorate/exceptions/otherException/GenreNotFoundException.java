package ru.yandex.practicum.filmorate.exceptions.otherException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
public class GenreNotFoundException extends ResponseStatusException {
    public GenreNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Такой жанр не существует");
    }
}

