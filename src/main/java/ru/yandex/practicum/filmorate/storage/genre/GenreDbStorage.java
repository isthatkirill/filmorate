package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM genres ORDER BY genre_id ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeGenre(rs, rowNum));
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        String query = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(query, (rs, rowNum) -> makeGenre(rs, rowNum), id);

        if (genres.isEmpty()) {
            log.info("Genre with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Genre found: {} {}", genres.get(0).getId(), genres.get(0).getName());
        return Optional.of(genres.get(0));
    }

    @Override
    public Film saveFilmGenres(Film film) {
        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = film.getGenres().stream()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(query, batchArgs);
        return film;
    }

    @Override
    public void addGenreForFilmById(Long filmId, long genreId) {
        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(query, filmId, genreId);
    }

    @Override
    public void deleteGenresByFilmId(Long filmId) {
        String query = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(query, filmId);
    }

    @Override
    public void setGenresFilms(List<Film> films) {
        final Map<Long, Film> filmById = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT * from genres g, film_genres fg " +
                "WHERE fg.genre_id = g.genre_id AND fg.film_id IN (" + inSql + ")";

        jdbcTemplate.query(sqlQuery, rs -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.addGenre(makeGenre(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long id) {
        String query = "SELECT g.genre_id, g.name FROM film_genres as fg\n" +
                "LEFT JOIN genres AS g on g.genre_id = fg.genre_id\n" +
                "WHERE FILM_ID = ?\n" +
                "ORDER BY g.genre_id ASC";
        return new HashSet<>(jdbcTemplate.query(query, (rs, rowNum)
                -> makeGenre(rs, rowNum), id));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
