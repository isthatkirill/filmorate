package isthatkirill.storage.impl;

import isthatkirill.model.Director;
import isthatkirill.model.Film;
import isthatkirill.util.Mappers;
import isthatkirill.util.SqlQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import isthatkirill.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> get(Integer id) {
        List<Director> directors = jdbcTemplate.query(SqlQueries.FIND_DIRECTOR_BY_ID, Mappers.DIRECTOR_MAPPER, id);
        if (directors.isEmpty()) {
            log.info("Director with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Director found: {} {}", directors.get(0).getId(), directors.get(0).getName());
        return Optional.of(directors.get(0));
    }

    @Override
    public List<Director> getAll() {
        return jdbcTemplate.query(SqlQueries.FIND_ALL_DIRECTORS, Mappers.DIRECTOR_MAPPER);
    }

    @Override
    public Film saveFilmDirectors(Film film) {
        List<Object[]> batchArgs = film.getDirectors().stream()
                .map(director -> new Object[]{film.getId(), director.getId()})
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(SqlQueries.SAVE_FILM_DIRECTORS, batchArgs);
        return film;
    }

    @Override
    public void addDirectorForFilmById(Long filmId, long directorId) {
        jdbcTemplate.update(SqlQueries.ADD_DIRECTOR_FOR_FILM_BY_ID, filmId, directorId);
    }

    @Override
    public void deleteDirectorsByFilmId(Long filmId) {
        jdbcTemplate.update(SqlQueries.DELETE_DIRECTORS_BY_FILM_ID, filmId);
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
            film.addDirector(makeDirector(rs));
        }, films.stream().map(Film::getId).toArray());
    }

    @Override
    public Director save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SqlQueries.SAVE_DIRECTOR, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        jdbcTemplate.update(SqlQueries.UPDATE_DIRECTOR, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Director director) {
        this.jdbcTemplate.update(SqlQueries.DELETE_DIRECTOR,director.getId());
    }

    @Override
    public Set<Director> getDirectorByFilmId(Long id) {
        return new HashSet<>(jdbcTemplate.query(SqlQueries.GET_DIRECTOR_BY_FILM_ID, Mappers.DIRECTOR_MAPPER, id));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}