package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private UserService userService;
    private FilmController filmController;
    private static ValidatorFactory validatorFactory;
    private static Validator validator;
    private static String TOO_LONG_DESCRIPTION = "TOO_LONG".repeat(100);

    @BeforeAll
    public static void create() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void destroy() {
        validatorFactory.close();
    }

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);
    }

    @Test
    public void validFilmTest() {
        Film film = Film.builder()
                .name("TestFilm")
                .description("Test description for TestFilm")
                .duration(155)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.addFilm(film);
        var validates = validator.validate(film);

        assertEquals(0, validates.size());
        assertEquals(film, filmController.getAllFilms().get(0));
        assertEquals(1, filmController.getAllFilms().get(0).getId());
    }

    @Test
    public void invalidFilmNameTest() {
        Film film = Film.builder()
                .name("")
                .description("Test description for TestFilm")
                .duration(155)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.addFilm(film);

        var validates = validator.validate(film);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void invalidDescriptionTest() {
        Film film = Film.builder()
                .name("TestName")
                .description(TOO_LONG_DESCRIPTION)
                .duration(155)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.addFilm(film);

        var validates = validator.validate(film);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void invalidDurationTest() {
        Film film = Film.builder()
                .name("TestName")
                .description("Test description")
                .duration(-999)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.addFilm(film);


        var validates = validator.validate(film);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void invalidReleaseDateTest() {
        Film film = Film.builder()
                .name("TestName")
                .description("Test description")
                .duration(155)
                .releaseDate(LocalDate.of(1111, 11, 11)).build();


        Exception e = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals(0, filmController.getAllFilms().size());
        assertEquals("The release time of the film can't be earlier FIRST_FILM_DATE", e.getMessage());
    }

    @Test
    public void updateValidFilmTest() {
        Film film = Film.builder()
                .name("TestFilm")
                .description("Test description for TestFilm")
                .duration(155)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.addFilm(film);

        Film updated = Film.builder()
                .name("TestFilm")
                .description("New description")
                .id(film.getId())
                .duration(155)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();
        filmController.updateFilm(updated);

        assertEquals(updated, filmController.getAllFilms().get(0));
    }

    @Test
    public void updateNonExistentFilmTest() {
        Film film = Film.builder()
                .name("TestFilm")
                .description("Update non-existent film")
                .duration(155)
                .id(5L)
                .releaseDate(LocalDate.of(1999, 11, 12)).build();

        Exception e = assertThrows(FilmNotFoundException.class, () -> {
            filmController.updateFilm(film);
        });

        assertEquals("Film with id 5 not found", e.getMessage());
    }

    private void validationInfo(Set<ConstraintViolation<Film>> validates) {
        validates.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
    }

}