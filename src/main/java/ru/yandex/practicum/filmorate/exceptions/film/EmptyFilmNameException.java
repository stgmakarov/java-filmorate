package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class EmptyFilmNameException extends ResponseStatusException {
    public EmptyFilmNameException(){
        super(HttpStatus.BAD_REQUEST, "Имя фильма не может быть пустым");
    }
}