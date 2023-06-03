package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.Mappers.DIRECTOR_MAPPER;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> get(Integer id) {
        String query = "SELECT * FROM directors WHERE director_id = ?";
        List<Director> directors = jdbcTemplate.query(query, DIRECTOR_MAPPER, id);
        if (directors.isEmpty()) {
            log.info("Director with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Director found: {} {}", directors.get(0).getId(), directors.get(0).getName());
        return Optional.of(directors.get(0));
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, DIRECTOR_MAPPER);
    }

    @Override
    public Film saveFilmDirectors(Film film) {
        String query = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
        List<Object[]> batchArgs = film.getDirectors().stream()
                .map(director -> new Object[]{film.getId(), director.getId()})
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(query, batchArgs);
        return film;
    }

    @Override
    public void deleteDirectorsByFilmId(Long filmId) {
        String sql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);

    }

    @Override
    public void setDirectorsFilms(List<Film> films) {
        final Map<Long, Film> filmById = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT * from directors d, film_directors fd " +
                "WHERE fd.director_id = d.director_id AND fd.film_id IN (" + inSql + ")";

        jdbcTemplate.query(sqlQuery, rs -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.addDirector(makeDirector(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }

    @Override
    public Director save(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Director director) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        this.jdbcTemplate.update(sql,director.getId());
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}