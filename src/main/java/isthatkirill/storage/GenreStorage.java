package isthatkirill.storage;

import isthatkirill.model.Film;
import isthatkirill.model.Genre;

import java.util.*;

public interface GenreStorage {

    List<Genre> getAllGenres();

    Optional<Genre> findGenreById(int id);

    void setGenresFilms(List<Film> films);

    Set<Genre> getGenresByFilmId(Long filmId);

    Film saveFilmGenres(Film film);

    void addGenreForFilmById(Long filmId, long genreId);

    void deleteGenresByFilmId(Long filmId);
}
