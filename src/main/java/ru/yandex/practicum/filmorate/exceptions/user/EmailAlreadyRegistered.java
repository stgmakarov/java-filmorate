package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class EmailAlreadyRegistered extends ResponseStatusException {
    public EmailAlreadyRegistered(String email){
        super(HttpStatus.BAD_REQUEST,String.format("EMail %s уже зарегистрирован", email));
    }
}