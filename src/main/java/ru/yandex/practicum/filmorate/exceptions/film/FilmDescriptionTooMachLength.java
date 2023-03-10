package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDescriptionTooMachLength extends ResponseStatusException {
    public FilmDescriptionTooMachLength(int maxDeskLen){
        super(HttpStatus.BAD_REQUEST, String.format("Описание фильма не более %d символов", maxDeskLen));
    }
}
