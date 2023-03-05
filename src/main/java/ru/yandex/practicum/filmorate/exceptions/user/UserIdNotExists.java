package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class UserIdNotExists extends RuntimeException {
    public UserIdNotExists(int id){
        log.error(String.format("Пользователь с ИД %d не найден в системе", id));
    }
    public UserIdNotExists(String id){
        log.error(String.format("Пользователь с ИД %s не найден в системе", id));
    }
}
