package isthatkirill.storage;

import isthatkirill.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> findFilmById(Long id);

    List<Film> getPopularFilms(Integer count);

    List<Film> findFilmListDirectorById(long director);

    List<Film> getCommonFilmsByUsers(Long userId, Long friendId);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Long genreId, Short year);

    List<Film> getPopularFilmsByGenre(Integer count, Long genreId);

    List<Film> getPopularFilmsByYear(Integer count, Short year);

    List<Film> getRecommendations(Long userId);

    List<Film> searchFilmsByTitle(String query);

    List<Film> searchFilmByDirector(String query);

    List<Film> searchFilmsByTitleAndDirector(String query);

    void deleteFilm(Long id);
}
