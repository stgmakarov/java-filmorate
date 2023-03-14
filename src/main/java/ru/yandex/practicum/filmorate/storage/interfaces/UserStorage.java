package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * @author Stanislav Makarov
 */
public interface UserStorage {
    User create(User user);
    User update( User user);
    void checker(User user, boolean updateFlag);
    List<User> getListOfUsers();
    List<User> getListOfUsers(List<Integer> users);
    User getUserById(int userId);
    boolean addFriend(int userId, int friendId);
    boolean removeFriend(int userId, int friendId);

}
