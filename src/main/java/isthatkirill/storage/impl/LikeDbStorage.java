package isthatkirill.storage.impl;

import isthatkirill.storage.LikeStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static isthatkirill.util.Mappers.LIKE_MAPPER;
import static isthatkirill.util.SqlQueries.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            jdbcTemplate.update(ADD_LIKE, filmId, userId);
        } catch (DataAccessException ignored) {
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(LIKES_BY_FILM_ID, LIKE_MAPPER, filmId));
    }


}
