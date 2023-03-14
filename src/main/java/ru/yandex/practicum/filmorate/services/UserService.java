package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.user.FriendAddError;
import ru.yandex.practicum.filmorate.exceptions.user.FriendRemoveError;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

/**
 * @author Stanislav Makarov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<Integer> getFriends(int userId){
        return List.copyOf(userStorage.getUserById(userId).getFriends());
    }

    public void makeFriendship(int userId1, int userId2) {
        if(userId1==userId2){
            throw new FriendAddError("Нельзя добавлять самого себя в друзья");
        }
        if(!addFriend(userId1, userId2)||!addFriend(userId2, userId1))throw new FriendAddError("Уже друзья");
    }

    public void removeFriendship(int userId1, int userId2){
       if(!removeFriend(userId1, userId2)||!removeFriend(userId2, userId1)) throw new FriendRemoveError();
    }

    public boolean checkFriendship(int userId1, int userId2){
        return userStorage.getUserById(userId1).getFriends().contains(userId2);
    }

    public List<Integer> getOursFriendList(int userId1, int userId2){
        Set<Integer> friendSet1 = userStorage.getUserById(userId1).getFriends();
        Set<Integer> friendSet2 = userStorage.getUserById(userId2).getFriends();
        Set<Integer> friends = intersection(friendSet1, friendSet2);
        return List.copyOf(friends);
    }

    private static Set<Integer> intersection(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    private boolean addFriend(int userId, int friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    private boolean removeFriend(int userId, int friendId){
        return userStorage.removeFriend(userId, friendId);
    }
}
