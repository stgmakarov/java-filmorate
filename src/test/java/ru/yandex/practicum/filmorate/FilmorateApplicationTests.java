package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final UserService userService;

    @BeforeAll
    void initUser() {
        userStorage.create(new User(0
                , "assd@hagel.com"
                , "login"
                , "test"
                , LocalDate.of(1983, 1, 1)
                , new HashSet<>()
                , new HashSet<>()
        ));

        userStorage.create(new User(0
                , "assd1@hagel.com"
                , "login1"
                , "test1"
                , LocalDate.of(1983, 1, 1)
                , new HashSet<>()
                , new HashSet<>()
        ));

        userService.makeFriendship(1, 2);
    }

    @Test
    public void testFindUserById() {


        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void checkFriendship() {
        assertThat(userService.checkFriendship(1, 2)).isTrue();
        assertThat(userService.checkFriendship(2, 1)).isFalse();
    }

}
