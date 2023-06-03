package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;

    public Film addLike(Long filmId, Long userId) {
        Film film = checkFilmExistent(filmId);
        userService.checkUserExistent(userId);
        likeStorage.addLike(filmId, userId);
        log.info("Film {} liked by user {}", filmId, userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = checkFilmExistent(filmId);
        userService.checkUserExistent(userId);
        likeStorage.deleteLike(filmId, userId);
        log.info("User {} deleted his like from film {}", userId, filmId);
        return film;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genreStorage.saveFilmGenres(film);
        }
        log.info("Film added: {}", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        checkFilmExistent(film.getId());
        filmStorage.updateFilm(film);
        genreStorage.deleteGenresByFilmId(film.getId());
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genreStorage.saveFilmGenres(film);
        }
        log.info("Film updated: {}", film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        genreStorage.setGenresFilms(allFilms);
        log.info("Number of films: {}", allFilms.size());
        return allFilms;
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        genreStorage.setGenresFilms(popularFilms);
        log.info("Returned {} most popular films", count);
        return popularFilms;
    }

    public Film getFilmById(Long id) {
        log.info("Get film by id = {} ", id);
        Film film = checkFilmExistent(id);
        genreStorage.setGenresFilms(List.of(film));
        return film;
    }

    public Film checkFilmExistent(Long id) {
        return filmStorage
                .findFilmById(id)
                .orElseThrow(() -> new EntityNotFoundException(Film.class, "Id:" + id));
    }


}
