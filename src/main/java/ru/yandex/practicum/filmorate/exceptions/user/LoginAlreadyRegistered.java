package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class LoginAlreadyRegistered extends RuntimeException {
    public LoginAlreadyRegistered(String login){
        log.error(String.format("Логин %s уже зарегистрирован", login));
    }
}
