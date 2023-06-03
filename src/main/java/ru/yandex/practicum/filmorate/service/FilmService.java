package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OnUpdateException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private Long id = 0L;
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addLike(Long filmId, Long userId) {
        Film film = checkFilmExistent(filmId);
        checkUserExistent(userId);
        if (film.getLikes().contains(userId)) {
            throw new OnUpdateException("You have already liked this film");
        }
        film.addLike(userId);
        log.info("Film " + filmId + " liked by user " + userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = checkFilmExistent(filmId);
        checkUserExistent(userId);
        if (!film.getLikes().contains(userId)) {
            throw new OnUpdateException("There is no your like on this film");
        }
        film.deleteLike(userId);
        log.info("User " + userId + " deleted his like from film " + filmId);
        return film;
    }

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("The release time of the film can't be earlier FIRST_FILM_DATE");
        }
        film.setId(++id);
        filmStorage.addFilm(film);
        log.info("Film added: " + film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        checkFilmExistent(film.getId());
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("The release time of the film can't be earlier FIRST_FILM_DATE");
        }
        filmStorage.updateFilm(film);
        log.info("Film updated: " + film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        log.info("Number of films: " + allFilms.size());
        return allFilms;
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Returned " + count + " most popular films");
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(Long id) {
        return checkFilmExistent(id);
    }

    private Film checkFilmExistent(Long id) {
        return filmStorage
                .findFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film with id " + id + " not found"));
    }

    private User checkUserExistent(Long id) {
        return userService.checkUserExistent(id);
    }

}
