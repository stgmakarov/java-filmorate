package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class LoginAlreadyRegistered extends ResponseStatusException {
    public LoginAlreadyRegistered(String login) {
        super(HttpStatus.BAD_REQUEST, String.format("Логин %s уже зарегистрирован", login));
    }
}
