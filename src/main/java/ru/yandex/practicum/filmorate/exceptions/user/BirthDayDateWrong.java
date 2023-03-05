package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class BirthDayDateWrong extends RuntimeException {
    public BirthDayDateWrong(){
        log.error("дата рождения не может быть в будущем");
    }
}