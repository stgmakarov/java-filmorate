package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class EmailAlreadyRegistered extends RuntimeException {
    public EmailAlreadyRegistered(String email){
        log.error(String.format("EMail %s уже зарегистрирован", email));
    }
}