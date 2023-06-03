package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserStorage userStorage;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1L)
                .login("TestLogin")
                .name("TestName")
                .email("testemail@email.ru")
                .birthday(LocalDate.of(2002, 2, 3))
                .build();
        userStorage.addUser(user);
    }

    @Test
    public void addUserTest() {
        List<User> users = userStorage.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("login", "TestLogin")
                .hasFieldOrPropertyWithValue("name", "TestName")
                .hasFieldOrPropertyWithValue("email", "testemail@email.ru")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2002, 2, 3));
    }

    @Test
    public void updateUserTest() {
        User testUser = User.builder()
                .id(1L)
                .login("TestLoginNew")
                .name("TestNameNew")
                .email("testemailnew@email.ru")
                .birthday(LocalDate.of(2004, 2, 3))
                .build();
        userStorage.updateUser(testUser);
        Optional<User> dbUser = userStorage.findUserById(1L);

        assertThat(dbUser)
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(u).hasFieldOrPropertyWithValue("login", "TestLoginNew");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "TestNameNew");
                    assertThat(u).hasFieldOrPropertyWithValue("email", "testemailnew@email.ru");
                    assertThat(u).hasFieldOrPropertyWithValue("birthday",
                            LocalDate.of(2004, 2, 3));
                });
    }

    @Test
    public void getAllUsersTest() {
        assertThat(userStorage.getAllUsers()).hasSize(1);

        User secondUser = User.builder()
                .id(2L)
                .login("TestLoginSecond")
                .name("TestNameSecond")
                .email("testemailsecond@email.ru")
                .birthday(LocalDate.of(2004, 1, 3))
                .build();
        userStorage.addUser(secondUser);

        List<User> users = userStorage.getAllUsers();

        assertThat(users)
                .satisfies(u -> {
                    assertThat(u).hasSize(2);
                    assertThat(u.get(0).getName()).isEqualTo("TestName");
                    assertThat(u.get(1).getName()).isEqualTo("TestNameSecond");
                });
    }

    @Test
    public void findUserByIdTest() {
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(u).hasFieldOrPropertyWithValue("login", "TestLogin");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "TestName");
                    assertThat(u).hasFieldOrPropertyWithValue("email", "testemail@email.ru");
                    assertThat(u).hasFieldOrPropertyWithValue("birthday",
                            LocalDate.of(2002, 2, 3));
                });
    }

    @Test
    public void findNonExistentUserTest() {
        Optional<User> userOptional = userStorage.findUserById(9999);

        assertThat(userOptional)
                .isNotPresent();
    }
}