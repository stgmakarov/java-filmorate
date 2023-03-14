package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.user.FriendAddError;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */

public class UserServiceTests {
    private UserService userService;
    private UserStorage userStorage;
    @BeforeEach
    public void initUserService(){
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        for(int i =1;i<=5;i++){
            User user = new User(0, "login" + i);
            user.setEmail("email"+i+"@gmail.com");
            user.setName("My Name");
            user.setBirthday(LocalDate.of(1983,1,1));
            userStorage.create(user);
        }
    }

    @Test
    public void testAddFriendIAm(){
        assertThrowsExactly(FriendAddError.class,  () -> userService.makeFriendship(1,1));
    }

    @Test
    public void testAddFriend(){
        assertDoesNotThrow(()->userService.makeFriendship(1,2));
        assertDoesNotThrow(()->userService.makeFriendship(1,3));
        assertTrue(userService.checkFriendship(2,1));
        assertTrue(userService.checkFriendship(1,2));
    }

    @Test
    public void testRemoveFriend(){
        assertDoesNotThrow(()->userService.makeFriendship(1,2));
        assertDoesNotThrow(()->userService.makeFriendship(1,3));
        assertTrue(userService.checkFriendship(1,2));
        assertTrue(userService.checkFriendship(2,1));
        assertDoesNotThrow(() -> userService.removeFriendship(2,1));
        assertFalse(userService.checkFriendship(1,2));
        assertFalse(userService.checkFriendship(2,1));
    }

    @Test
    public void getOursFriendListTest(){
        userService.makeFriendship(1,2);
        userService.makeFriendship(1,3);
        userService.makeFriendship(4,2);
        userService.makeFriendship(4,5);
        userService.makeFriendship(4,3);
        assertTrue(userService.getOursFriendList(1,4).get(0)==2
                && userService.getOursFriendList(1,4).get(1)==3);
    }

}
