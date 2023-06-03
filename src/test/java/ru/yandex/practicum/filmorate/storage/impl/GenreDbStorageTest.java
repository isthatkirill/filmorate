package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;


@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreStorage genreStorage;
    private final FilmStorage filmStorage;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .id(1L)
                .name("Test")
                .description("Test description")
                .duration(50)
                .releaseDate(LocalDate.of(2002, 9, 15))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmStorage.addFilm(film);
    }

    @Test
    public void getAllGenresTest() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertThat(genres)
                .satisfies(g -> {
                    assertThat(g).hasSize(6);
                    assertThat(g)
                            .extracting(Genre::getName)
                            .containsExactly("Комедия", "Драма", "Мультфильм", "Триллер",
                                    "Документальный", "Боевик");
                });
    }

    @Test
    public void findGenreByIdTest() {
        Optional<Genre> optionalGenre = genreStorage.findGenreById(1);

        assertThat(optionalGenre)
                .isPresent()
                .get()
                .satisfies(g -> {
                    assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия");
                    assertThat(g).hasFieldOrPropertyWithValue("id", 1);
                });
    }

    @Test
    public void findNonExistentGenreTest() {
        Optional<Genre> optionalGenre = genreStorage.findGenreById(9999);

        assertThat(optionalGenre)
                .isNotPresent();
    }

    @Test
    public void addGenreForFilmByIdTest() {
        genreStorage.addGenreForFilmById(film.getId(), 1);
        genreStorage.addGenreForFilmById(film.getId(), 2);
        Set<Genre> genres = genreStorage.getGenresByFilmId(film.getId());

        assertThat(genres)
                .hasSize(2)
                .map(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма");
    }

    @Test
    public void deleteGenresByFilmIdTest() {
        genreStorage.addGenreForFilmById(film.getId(), 1);
        genreStorage.deleteGenresByFilmId(film.getId());
        Set<Genre> genres = genreStorage.getGenresByFilmId(film.getId());

        assertThat(genres)
                .isEmpty();
    }

    @Test
    public void getGenreByFilmIdTest() {
        genreStorage.addGenreForFilmById(film.getId(), 1);
        Set<Genre> genres = genreStorage.getGenresByFilmId(1L);

        assertThat(genres)
                .hasSize(1)
                .map(Genre::getName)
                .containsExactlyInAnyOrder("Комедия");
    }

    @Test
    public void getGenreByNonExistentFilmIdTest() {
        Set<Genre> genres = genreStorage.getGenresByFilmId(1111L);

        assertThat(genres).isEmpty();
    }

    @Test
    void setGenresFilmsTest() {
        addGenreForFilmByIdTest();
        genreStorage.setGenresFilms(List.of(film));

        assertThat(film).satisfies(f -> {
            assertThat(f.getGenres().stream()
                    .map(Genre::getName)).containsExactlyInAnyOrder("Комедия", "Драма");
        });
    }
}