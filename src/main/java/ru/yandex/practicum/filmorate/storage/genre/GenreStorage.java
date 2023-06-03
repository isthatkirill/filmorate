package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
