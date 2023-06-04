package isthatkirill.storage.impl;

import isthatkirill.model.Film;
import isthatkirill.storage.FilmStorage;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import isthatkirill.model.Director;
import isthatkirill.model.Mpa;
import isthatkirill.storage.DirectorStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDBStorageTest {

    private final DirectorStorage directorStorage;
    private final FilmStorage filmStorage;
    private Film film;
    private Director director1;
    private Director director2;

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

        director1 = Director.builder()
                .id(1L)
                .name("Test first")
                .build();
        directorStorage.save(director1);

        director2 = Director.builder()
                .id(2L)
                .name("Test second")
                .build();
        directorStorage.save(director2);
    }

    @Test
    public void getDirectorTest() {
        Optional<Director> optionalDirector = directorStorage.get(1L);

        assertThat(optionalDirector)
                .isPresent()
                .get()
                .satisfies(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Test first");
                });
    }

    @Test
    public void getAllDirectorsTest() {
        List<Director> directors = directorStorage.getAll();

        assertThat(directors)
                .satisfies(f -> {
                    assertThat(f).hasSize(2);
                    assertThat(f.stream()
                            .map(Director::getName)
                            .collect(Collectors.toList()))
                            .containsExactly("Test first", "Test second");
                });
    }

    @Test
    void addDirectorTest() {
        assertThat(directorStorage.getAll()).hasSize(2);
    }

    @Test
    public void updateDirectorTest() {
        Director testDirector = Director.builder()
                .id(1L)
                .name("Test first updated")
                .build();
        directorStorage.update(testDirector);
        Optional<Director> optionalDirector = directorStorage.get(1L);

        assertThat(optionalDirector)
                .isPresent()
                .get()
                .satisfies(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Test first updated");
                });
    }

    @Test
    void deleteDirectorTest() {
        directorStorage.delete(director1);
        Optional<Director> directorDeleteOptional = directorStorage.get(1L);

        assertFalse(directorDeleteOptional.isPresent());
    }

    @Test
    public void addFilmDirectorsTest() {
        directorStorage.addDirectorForFilmById(film.getId(), 1L);
        directorStorage.addDirectorForFilmById(film.getId(), 2L);
        Set<Director> directors = directorStorage.getDirectorByFilmId(film.getId());

        assertThat(directors)
                .hasSize(2)
                .map(Director::getName)
                .containsExactlyInAnyOrder("Test first", "Test second");
    }

    @Test
    public void deleteFilmDirectorsTest() {
        directorStorage.addDirectorForFilmById(film.getId(), 1);
        directorStorage.deleteDirectorsByFilmId(film.getId());
        Set<Director> directors = directorStorage.getDirectorByFilmId(film.getId());

        assertThat(directors)
                .isEmpty();
    }

    @Test
    void setDirectorsFilmsTest() {
        addFilmDirectorsTest();
        directorStorage.setDirectorsFilms(List.of(film));

        assertThat(film).satisfies(f -> assertThat(f.getDirectors().stream()
                .map(Director::getName)).containsExactlyInAnyOrder("Test first", "Test second"));
    }

}


