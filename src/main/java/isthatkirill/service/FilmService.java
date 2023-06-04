package isthatkirill.service;

import isthatkirill.annotation.SaveUserFeed;
import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.model.Director;
import isthatkirill.model.Film;
import isthatkirill.model.Genre;
import isthatkirill.model.enums.EventType;
import isthatkirill.model.enums.Operation;
import isthatkirill.storage.DirectorStorage;
import isthatkirill.storage.FilmStorage;
import isthatkirill.storage.GenreStorage;
import isthatkirill.storage.LikeStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Set<Genre> genres = film.getGenres();
        Set<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            for (Director director : directors) {
                if (directorStorage.get(director.getId()).isEmpty()) {
                    throw new EntityNotFoundException(Director.class, "Director not found id - " + director.getId());
                }
            }
        }
        filmStorage.addFilm(film);
        if (genres != null && !genres.isEmpty()) {
            genreStorage.saveFilmGenres(film);
        }
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

    public List<Film> sortFilmDirector(Long directorId, String sort) {
        List<Film> allFilms = filmStorage.findFilmListDirectorById(directorId);
        genreStorage.setGenresFilms(allFilms);
        directorStorage.setDirectorsFilms(allFilms);

        directorStorage.get(directorId)
                .orElseThrow(() -> new EntityNotFoundException(Director.class, "There is no director " + directorId + " id in the database"));

        if (sort.equals("year")) {
            log.info("Get films director - {} sorted by release years. ", directorId);
            return sortFilmsByReleaseYear(allFilms);
        } else if (sort.equals("likes")) {
            log.info("Get films director - {} sorted by likes. ", directorId);
            return sortFilmsByLikes(allFilms);
        } else {
            throw new EntityNotFoundException(Director.class, "Response sort list top films by Director - " + directorId);
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

    private List<Film> sortFilmsByReleaseYear(List<Film> films) {
        return films.stream()
                .sorted(Comparator.comparing((Film film) -> film.getReleaseDate().getYear()))
                .collect(Collectors.toList());
    }

    private List<Film> sortFilmsByLikes(List<Film> films) {
        return films.stream()
                .sorted((film1, film2) -> likeStorage.getLikesByFilmId(film2.getId()).size() -
                        likeStorage.getLikesByFilmId(film1.getId()).size())
                .collect(Collectors.toList());
    }
}
