package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class LoginWrong extends ResponseStatusException {
    public LoginWrong(String login, String errorText){
        super(HttpStatus.BAD_REQUEST,String.format("Логин \"%s\" не корректен: %s", login, errorText));
    }
}
