package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.annotation.SaveUserFeed;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;
    private final DirectorStorage directorStorage;


    @SaveUserFeed(
            value = EventType.LIKE,
            operation = Operation.ADD,
            userIdPropertyName = "userId",
            entityIdPropertyName = "filmId"
    )
    public Film addLike(Long filmId, Long userId) {
        Film film = checkFilmExistent(filmId);
        userService.checkUserExistent(userId);
        likeStorage.addLike(filmId, userId);
        log.info("Film {} liked by user {}", filmId, userId);
        return film;
    }

    @SaveUserFeed(
            value = EventType.LIKE,
            operation = Operation.REMOVE,
            userIdPropertyName = "userId",
            entityIdPropertyName = "filmId"
    )
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
        Set<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directorStorage.saveFilmDirectors(film);
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
        directorStorage.deleteDirectorsByFilmId(film.getId());
        Set<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directorStorage.saveFilmDirectors(film);
        }
        log.info("Film updated: {}", film.getName());
        return film;
    }

    public void deleteFilm(Long id) {
        checkFilmExistent(id);
        log.info("Film id = {} deleted", id);
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        genreStorage.setGenresFilms(allFilms);
        directorStorage.setDirectorsFilms(allFilms);
        log.info("Number of films: {}", allFilms.size());
        return allFilms;
    }

    public List<Film> getPopularFilms(Integer count, Long genreId, Short year) {
        List<Film> popularFilms;
        if (genreId != null && year != null) {
            popularFilms = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (year != null) {
            popularFilms = filmStorage.getPopularFilmsByYear(count, year);
        } else if (genreId != null) {
            popularFilms = filmStorage.getPopularFilmsByGenre(count, genreId);
        } else {
            popularFilms = filmStorage.getPopularFilms(count);
        }
        genreStorage.setGenresFilms(popularFilms);
        directorStorage.setDirectorsFilms(popularFilms);
        log.info("Returned {} most popular films", count);
        return popularFilms;
    }

    public Film getFilmById(Long id) {
        log.info("Get film by id = {} ", id);
        Film film = checkFilmExistent(id);
        genreStorage.setGenresFilms(List.of(film));
        directorStorage.setDirectorsFilms(List.of(film));
        return film;
    }

    public List<Film> searchFilms(String query, List<String> searchBy) {
        List<Film> filmsByQuery;
        if (searchBy.size() == 1 && searchBy.get(0).equals("title")) {
            filmsByQuery = filmStorage.searchFilmsByTitle(query);
        } else if (searchBy.size() == 1 && searchBy.get(0).equals("director")) {
            filmsByQuery = filmStorage.searchFilmByDirector(query);
        } else {
            filmsByQuery = filmStorage.searchFilmsByTitleAndDirector(query);
        }
        genreStorage.setGenresFilms(filmsByQuery);
        directorStorage.setDirectorsFilms(filmsByQuery);
        log.info("Search film by query = {}", query);
        return filmsByQuery;
    }

    public Film checkFilmExistent(Long id) {
        return filmStorage
                .findFilmById(id)
                .orElseThrow(() -> new EntityNotFoundException(Film.class, "Id:" + id));
    }

    public List<Film> sortFilmDirector(Integer directorId, String sort) {
        List<Film> allFilms = filmStorage.findFilmListDirectorById(directorId);
        genreStorage.setGenresFilms(allFilms);
        directorStorage.setDirectorsFilms(allFilms);
        if (directorStorage.get(directorId).isPresent()) {
            if (sort.equals("year")) {
                log.info("Get films director - {} sorted by release years. ", directorId);
                return allFilms.stream().sorted(Comparator.comparing((Film film) -> film.getReleaseDate().getYear()))
                        .collect(Collectors.toList());
            } else if (sort.equals("likes")) {
                log.info("Get films director - {} sorted by likes. ", directorId);
                return allFilms.stream().sorted((film1, film2) -> likeStorage.getLikesByFilmId(film2.getId()).size() -
                                likeStorage.getLikesByFilmId(film1.getId()).size())
                        .collect(Collectors.toList());
            } else {
                log.info("Response sort list top films by Director - {}", directorId);
                throw new EntityNotFoundException(Director.class, "Response sort list top films by Director - " + directorId);
            }
        } else {
            log.info("There is no director - {} id in the database ", directorId);
            throw new EntityNotFoundException(Director.class, "There is no director " + directorId + " id in the database");
        }
    }

    public List<Film> getCommonFilmsByUsers(Long userId, Long friendId) {
        userService.checkUserExistent(userId);
        userService.checkUserExistent(friendId);
        List<Film> commonFilms = filmStorage.getCommonFilmsByUsers(userId, friendId);
        genreStorage.setGenresFilms(commonFilms);
        log.info("User with id {} requested a list of mutual films with user {}", userId, friendId);
        return commonFilms;
    }

    public List<Film> getRecommendations(Long id) {
        userService.checkUserExistent(id);
        List<Film> recommendations = filmStorage.getRecommendations(id);
        genreStorage.setGenresFilms(recommendations);
        log.info("Returned recommended films for user {}", id);
        return recommendations;
    }
}
