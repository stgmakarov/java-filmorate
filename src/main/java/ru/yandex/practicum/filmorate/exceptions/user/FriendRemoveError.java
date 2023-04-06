package ru.yandex.practicum.filmorate.exceptions.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Stanislav Makarov
 */
@Slf4j
public class FriendRemoveError extends ResponseStatusException {
    public FriendRemoveError() {
        super(HttpStatus.NOT_FOUND, "Ошибка удаления друга: друг с таким ИД не найден в перечне друзей");
    }
}
