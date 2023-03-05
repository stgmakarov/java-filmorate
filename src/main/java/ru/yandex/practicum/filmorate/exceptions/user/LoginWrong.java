package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class LoginWrong extends RuntimeException {
    public LoginWrong(String login, String errorText){
        log.error(String.format("Логин \"%s\" не корректен: %s", login, errorText));
    }
}
