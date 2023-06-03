package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Optional<Director> get(Integer id);

    List<Director> getAll();

    Director save(Director director);

    Director update(Director director);

    void delete(Director director);

    Film saveFilmDirectors(Film film);

    void deleteDirectorsByFilmId(Long filmId);

    void setDirectorsFilms(List<Film> films);

}