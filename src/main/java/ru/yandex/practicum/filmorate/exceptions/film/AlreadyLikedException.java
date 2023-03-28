package ru.yandex.practicum.filmorate.exceptions.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class AlreadyLikedException extends ResponseStatusException {
    public AlreadyLikedException() {
        super(HttpStatus.BAD_REQUEST, "Ошибка, лайк уже был поставлен");
    }
}
