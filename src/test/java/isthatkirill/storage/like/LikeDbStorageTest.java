package isthatkirill.storage.like;

import isthatkirill.model.Film;
import isthatkirill.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import isthatkirill.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDbStorageTest {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private Film film;
    private User firstUser;
    private User secondUser;

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

        film = Film.builder()
                .id(1L)
                .name("Test")
                .description("Test description")
                .duration(50)
                .releaseDate(LocalDate.of(2002, 9, 15))
                .mpa(new Mpa(2, "PG"))
                .build();
        filmStorage.addFilm(film);
    }

    @Test
    public void addLikeTest() {
        likeStorage.addLike(1L, 1L);
        Set<Long> likes = likeStorage.getLikesByFilmId(1L);

        assertThat(likes)
                .hasSize(1)
                .containsExactlyInAnyOrder(1L);
    }

    @Test
    public void addLikeByTwoUsersTest() {
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        Set<Long> likes = likeStorage.getLikesByFilmId(1L);

        assertThat(likes)
                .hasSize(2)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void deleteLikeTest() {
        addLikeByTwoUsersTest();
        likeStorage.deleteLike(1L, 2L);
        Set<Long> likes = likeStorage.getLikesByFilmId(1L);

        assertThat(likes)
                .hasSize(1)
                .containsExactlyInAnyOrder(1L);
    }

    @Test
    public void getLikesByFilmIdTest() {
        addLikeByTwoUsersTest();
        Set<Long> likes = likeStorage.getLikesByFilmId(1L);

        assertThat(likes)
                .hasSize(2)
                .containsExactlyInAnyOrder(1L, 2L);
    }
}