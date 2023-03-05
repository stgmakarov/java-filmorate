package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.user.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stanislav Makarov
 */
public class UserControllerTest {
    UserController userController;

    @BeforeEach
    void initUserController() {
        userController = new UserController();
    }

    @Test
    void testUserCreationOk() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);
        assertEquals(user.getId(), 1);

        assertEquals(userController.getAllUsers().get(0).getLogin(), user.getLogin());
        assertEquals(userController.getUser("1").getLogin(), user.getLogin());
    }

    @Test
    void testUserUpdateOk() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user = userController.create(user);

        user.setName("testuser2");
        user.setEmail("test2@test.com");
        User user2 = userController.update(user);

        assertEquals(user2.getName(), user.getName());
        assertEquals(user2.getEmail(), user.getEmail());
    }

    @Test
    void testUserUpdateFail() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user.setName("testuser2");
        user.setEmail("test2 @test.com");

        assertThrowsExactly(EmailWrong.class, () -> userController.update(user));

    }

    @Test
    void testUserCreationFailEmailWrong() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.c o m");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        assertThrowsExactly(EmailWrong.class, () -> userController.create(user));
    }

    @Test
    void testUserCreationFailLoginWrong1() {
        User user = new User(0, "test user");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        assertThrowsExactly(LoginWrong.class, () -> userController.create(user));
    }

    @Test
    void testUserCreationFailLoginWrong2() {
        User user = new User(0, "");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrowsExactly(LoginWrong.class, () -> userController.create(user));
    }

    @Test
    void testUserCreationFailBirthday() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2200, 1, 1));
        assertThrowsExactly(BirthDayDateWrong.class, () -> userController.create(user));
    }

    @Test
    void testUserCreationFailEmailAlreadyRegistered() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        User user2 = new User(0, "testuser2");
        user2.setName("Test2");
        user2.setEmail("test@test.com");
        user2.setBirthday(LocalDate.of(2002, 1, 1));

        assertThrowsExactly(EmailAlreadyRegistered.class, () -> userController.create(user2));
    }

    @Test
    void testUserCreationFailLoginAlreadyRegistered() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        User user2 = new User(0, "testuser");
        user2.setName("Test2");
        user2.setEmail("test2@test.com");
        user2.setBirthday(LocalDate.of(2002, 1, 1));

        assertThrowsExactly(LoginAlreadyRegistered.class, () -> userController.create(user2));
    }

    @Test
    void testUserUpdateFailIdNotExists() {
        User user = new User(0, "testuser");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user.setId(9999);

        assertThrowsExactly(UserIdNotExists.class, () -> userController.update(user));
    }
}
