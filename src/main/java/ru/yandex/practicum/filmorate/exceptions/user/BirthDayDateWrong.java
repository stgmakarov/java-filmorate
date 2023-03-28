package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class BirthDayDateWrong extends ResponseStatusException {
    public BirthDayDateWrong() {
        super(HttpStatus.BAD_REQUEST, "дата рождения не может быть в будущем");
    }
}