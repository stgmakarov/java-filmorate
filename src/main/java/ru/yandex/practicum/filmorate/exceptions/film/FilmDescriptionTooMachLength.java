package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FilmDescriptionTooMachLength extends RuntimeException {
    public FilmDescriptionTooMachLength(int maxDeskLen){
        log.error(String.format("Описание фильма не более %d символов", maxDeskLen));
    }
}
