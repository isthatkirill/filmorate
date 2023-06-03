package isthatkirill.storage.impl;

import isthatkirill.model.Film;
import isthatkirill.storage.FilmStorage;
import isthatkirill.util.Mappers;
import isthatkirill.util.SqlQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import isthatkirill.exceptions.OnUpdateException;

import java.sql.Date;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        Optional<Film> filmMatch = findMatch(film);
        if (filmMatch.isPresent()) {
            film.setId(filmMatch.get().getId());
            log.info("Attempting to duplicate a film: {} {}", film.getId(), film.getName());
            throw new OnUpdateException("This film already exists");
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", Date.valueOf(film.getReleaseDate()));
        parameters.put("duration_minutes", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number filmId = insert.executeAndReturnKey(parameters);
        film.setId(filmId.longValue());
        log.info("Film create: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SqlQueries.UPDATE_FILM_BY_ID,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.info("Film update: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Received all films");
        return jdbcTemplate.query(SqlQueries.FIND_ALL_FILMS, Mappers.FILM_MAPPER);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        List<Film> films = jdbcTemplate.query(SqlQueries.FIND_FILM_BY_ID, Mappers.FILM_MAPPER, id);
        if (films.isEmpty()) {
            log.info("Film with id = {} not found", id);
            return Optional.empty();
        }

        log.info("Film found: {}", films.get(0).getId());
        return Optional.of(films.get(0));
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update(SqlQueries.DELETE_FILM, id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return jdbcTemplate.query(SqlQueries.FIND_POPULAR_FILMS, Mappers.FILM_MAPPER, count);
    }

    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Long genreId, Short year) {
        return jdbcTemplate.query(SqlQueries.FIND_POPULAR_FILMS_BY_GENRE_ID_AND_YEAR, Mappers.FILM_MAPPER, genreId, year, count);
    }

    public List<Film> getPopularFilmsByYear(Integer count, Short year) {
        return jdbcTemplate.query(SqlQueries.FIND_POPULAR_FILMS_BY_YEAR, Mappers.FILM_MAPPER, year, count);
    }

    public List<Film> getPopularFilmsByGenre(Integer count, Long genreId) {
        return jdbcTemplate.query(SqlQueries.FIND_POPULAR_FILMS_BY_GENRE_ID, Mappers.FILM_MAPPER, genreId, count);
    }

    @Override
    public List<Film> findFilmListDirectorById(long director) {
        return jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_DIRECTOR_ID, Mappers.FILM_MAPPER, director);
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        return jdbcTemplate.query(SqlQueries.SEARCH_FILMS_BY_TITLE, Mappers.FILM_MAPPER, query);
    }

    @Override
    public List<Film> searchFilmByDirector(String query) {
        return jdbcTemplate.query(SqlQueries.SEARCH_FILMS_BY_DIRECTOR, Mappers.FILM_MAPPER, query);
    }

    @Override
    public List<Film> searchFilmsByTitleAndDirector(String query) {
        return jdbcTemplate.query(SqlQueries.SEARCH_FILMS_BY_TITLE_AND_DIRECTOR, Mappers.FILM_MAPPER, query, query);
    }


    @Override
    public List<Film> getCommonFilmsByUsers(Long userId, Long friendId) {
        List<Long> commonFilms = jdbcTemplate.query(SqlQueries.GET_COMMON_FILMS_ID, Mappers.ID_FILM_MAPPER, userId, friendId);

        String inSql = String.join(",", Collections.nCopies(commonFilms.size(), "?"));

        return jdbcTemplate.query(
                String.format(SqlQueries.GET_FILMS_BY_LIST_IDS, inSql),
                Mappers.FILM_MAPPER,
                commonFilms.toArray());
    }

    public List<Film> getRecommendations(Long userId) {
        List<Long> similarUser = getSimilarUser(userId);

        if (similarUser.isEmpty()) {
            log.info("Not found similar users. Unable to make recommendations");
            return Collections.emptyList();
        }

        List<Long> diffFilms = jdbcTemplate.query(SqlQueries.GET_DIFFERENT_FILMS_ID, Mappers.ID_FILM_MAPPER, similarUser.get(0), userId);
        String inSql = String.join(",", Collections.nCopies(diffFilms.size(), "?"));

        return jdbcTemplate.query(
                String.format(SqlQueries.GET_FILMS_BY_LIST_IDS, inSql),
                Mappers.FILM_MAPPER,
                diffFilms.toArray());
    }

    private List<Long> getSimilarUser(Long userId) {
        return jdbcTemplate.query(SqlQueries.GET_SIMILAR_USER, Mappers.ID_SIMILAR_MAPPER, userId, userId);
    }

    private Optional<Film> findMatch(Film film) {
        List<Film> films = jdbcTemplate.query(SqlQueries.FIND_MATCH, Mappers.FILM_MAPPER,
                film.getName(),
                film.getReleaseDate(),
                film.getDuration());
        if (films.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(films.get(0));
        }
    }
}
