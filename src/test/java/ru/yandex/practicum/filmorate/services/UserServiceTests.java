package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.user.FriendAddError;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */

public class UserServiceTests {
    private UserService userService;
    @BeforeEach
    public void initUserService(){
        userService = new UserService();
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
