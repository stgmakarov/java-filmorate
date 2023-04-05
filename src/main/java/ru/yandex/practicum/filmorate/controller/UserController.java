package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getListOfUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userStorage.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userService.makeFriendship(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userService.removeFriendship(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") int id) {
        List<Integer> friendsIdList = userService.getFriends(id);
        return userStorage.getListOfUsers(friendsIdList);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getOursFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        List<Integer> friendsIdList = userService.getOursFriendList(id, otherId);
        return userStorage.getListOfUsers(friendsIdList);
    }
}

