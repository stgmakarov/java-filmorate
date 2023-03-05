package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class EmailWrong extends RuntimeException {
    public EmailWrong(String email, String errorText){
        log.error(String.format("EMail %s не корректен: %s", email, errorText));
    }
}
