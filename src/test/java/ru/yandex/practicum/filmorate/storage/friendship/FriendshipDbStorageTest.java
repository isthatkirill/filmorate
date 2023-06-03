package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDbStorageTest {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .login("TestLogin")
                .name("TestName")
                .email("testemail@email.ru")
                .birthday(LocalDate.of(2002, 2, 3))
                .build();
        userStorage.addUser(firstUser);

        secondUser = User.builder()
                .id(2L)
                .login("TestLoginTwo")
                .name("TestNameTwo")
                .email("testemailtwo@email.ru")
                .birthday(LocalDate.of(2004, 11, 23))
                .build();
        userStorage.addUser(secondUser);

        thirdUser = User.builder()
                .id(3L)
                .login("TestLoginThree")
                .name("TestNameThree")
                .email("testemailthree@email.ru")
                .birthday(LocalDate.of(2011, 5, 1))
                .build();
        userStorage.addUser(thirdUser);
    }

    @Test
    public void addFriendTest() {
        friendshipStorage.addFriend(1L, 2L);
        List<User> friends = friendshipStorage.getFriendsByUserId(1L);

        assertThat(friends).containsExactly(secondUser);
    }

    @Test
    public void getFriendsByUserIdTest() {
        friendshipStorage.addFriend(1L, 2L);
        friendshipStorage.addFriend(1L, 3L);
        List<User> friends = friendshipStorage.getFriendsByUserId(1L);

        assertThat(friends).containsExactly(secondUser, thirdUser);
    }

    @Test
    public void deleteFriendTest() {
        getFriendsByUserIdTest();
        friendshipStorage.deleteFriend(1L, 2L);
        List<User> friends = friendshipStorage.getFriendsByUserId(1L);

        assertThat(friends).containsExactly(thirdUser);
    }

    @Test
    public void checkIfFriendsTest() {
        friendshipStorage.addFriend(1L, 2L);
        boolean ifFriends = friendshipStorage.checkIfFriends(1L, 2L);

        assertThat(ifFriends).isTrue();

        ifFriends = friendshipStorage.checkIfFriends(2L, 1L);

        assertThat(ifFriends).isFalse();
    }

    @Test
    public void getCommonFriendsTest() {
        friendshipStorage.addFriend(1L, 3L);
        friendshipStorage.addFriend(2L, 3L);

        List<User> common = friendshipStorage.getCommonFriends(1L, 2L);

        assertThat(common).containsExactly(thirdUser);
    }
}