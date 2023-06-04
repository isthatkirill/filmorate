package isthatkirill.storage;

import isthatkirill.model.Director;
import isthatkirill.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    Optional<Director> get(Long id);

    List<Director> getAll();

    Director save(Director director);

    Director update(Director director);

    void delete(Director director);

    Film saveFilmDirectors(Film film);

    void addDirectorForFilmById(Long filmId, long directorId);

    void deleteDirectorsByFilmId(Long filmId);

    void setDirectorsFilms(List<Film> films);

    Set<Director> getDirectorByFilmId(Long id);
}