package isthatkirill.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import isthatkirill.model.Director;
import isthatkirill.model.Film;
import isthatkirill.model.Mpa;
import isthatkirill.model.User;
import isthatkirill.storage.DirectorStorage;
import isthatkirill.storage.FilmStorage;
import isthatkirill.storage.LikeStorage;
import isthatkirill.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private Film firstFilm;
    private Film secondFilm;
    private User firstUser;
    private User secondUser;
    private Director director;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                .id(1L)
                .name("Test first")
                .description("Test description first")
                .duration(50)
                .releaseDate(LocalDate.of(2001, 9, 25))
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(firstFilm);

        secondFilm = Film.builder()
                .id(2L)
                .name("Test second")
                .description("Test description second")
                .duration(125)
                .releaseDate(LocalDate.of(2014, 2, 15))
                .mpa(new Mpa(2, "PG"))
                .build();
        filmStorage.addFilm(secondFilm);

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

        director = Director.builder()
                .id(1L)
                .name("Test director")
                .build();
        directorStorage.save(director);
    }

    @Test
    public void addFilmTest() {
        assertThat(filmStorage.getAllFilms()).hasSize(2);
    }

    @Test
    public void findFilmByIdTest() {
        Optional<Film> optionalFilm = filmStorage.findFilmById(1L);

        assertThat(optionalFilm)
                .isPresent()
                .get()
                .satisfies(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Test first");
                    assertThat(f).hasFieldOrPropertyWithValue("description", "Test description first");
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 50);
                    assertThat(f).hasFieldOrPropertyWithValue("releaseDate",
                            LocalDate.of(2001, 9, 25));
                    assertThat(f.getMpa()).isEqualTo(new Mpa(1, "G"));
                });
    }

    @Test
    public void findNonExistentFilmTest() {
        Optional<Film> optionalFilm = filmStorage.findFilmById(9999L);

        assertThat(optionalFilm)
                .isNotPresent();
    }

    @Test
    public void updateFilmTest() {
        Film testFilm = Film.builder()
                .id(1L)
                .name("Test first updated")
                .description("Test description first updated")
                .duration(60)
                .releaseDate(LocalDate.of(2002, 9, 25))
                .mpa(new Mpa(2, "PG"))
                .build();
        filmStorage.updateFilm(testFilm);
        Optional<Film> optionalFilm = filmStorage.findFilmById(1L);

        assertThat(optionalFilm)
                .isPresent()
                .get()
                .satisfies(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Test first updated");
                    assertThat(f).hasFieldOrPropertyWithValue("description",
                            "Test description first updated");
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 60);
                    assertThat(f).hasFieldOrPropertyWithValue("releaseDate",
                            LocalDate.of(2002, 9, 25));
                    assertThat(f.getMpa()).isEqualTo(new Mpa(2, "PG"));
                });
    }

    @Test
    public void getAllFilmsTest() {
        List<Film> films = filmStorage.getAllFilms();

        assertThat(films)
                .satisfies(f -> {
                    assertThat(f).hasSize(2);
                    assertThat(f.stream()
                            .map(Film::getName)
                            .collect(Collectors.toList()))
                            .containsExactly("Test first", "Test second");
                });
    }

    @Test
    public void getPopularFilmsTest() {
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        likeStorage.addLike(2L, 2L);

        List<Film> films = filmStorage.getPopularFilms(1);
        assertThat(films.get(0).getName()).isEqualTo("Test first");

        films = filmStorage.getPopularFilms(2);
        assertThat(films.get(1).getName()).isEqualTo("Test second");
    }

    @Test
    public void findFilmListDirectorTest() {

        directorStorage.addDirectorForFilmById(firstFilm.getId(), director.getId());
        directorStorage.addDirectorForFilmById(secondFilm.getId(), director.getId());

        List<Film> films = filmStorage.findFilmListDirectorById(director.getId());

        assertThat(films)
                .satisfies(f -> {
                    assertThat(f).hasSize(2);
                    assertThat(f.stream()
                            .map(Film::getName)
                            .collect(Collectors.toList()))
                            .containsExactly("Test first", "Test second");
                });

    }



    @Test
    public void getCommonFilmsByUsersTest() {
        List<Film> films = filmStorage.getCommonFilmsByUsers(1L, 2L);
        assertThat(films.isEmpty());

        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        likeStorage.addLike(2L, 2L);

        films = filmStorage.getCommonFilmsByUsers(1L, 2L);

        assertThat(films.get(0).getName()).isEqualTo("Test first");
    }

    @Test
    public void getRecommendationsTest() {
        List<Film> films = filmStorage.getRecommendations(1L);
        assertThat(films.isEmpty());

        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        likeStorage.addLike(2L, 2L);

        films = filmStorage.getRecommendations(1L);

        assertThat(films.get(0).getName()).isEqualTo("Test second");

    }
}