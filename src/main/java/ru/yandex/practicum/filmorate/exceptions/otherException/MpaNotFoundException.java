package ru.yandex.practicum.filmorate.exceptions.otherException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
public class MpaNotFoundException extends ResponseStatusException {
    public MpaNotFoundException(){
        super(HttpStatus.NOT_FOUND,"Такой рейтинг не существует");
    }
}

