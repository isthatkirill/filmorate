package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.Mappers.REVIEW_MAPPER;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String USEFUL_PLUS = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
    private static final String USEFUL_MINUS = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("useful", 0);

        Number reviewId = insert.executeAndReturnKey(parameters);
        review.setReviewId(reviewId.longValue());
        return review;
    }

    @Override
    public List<Review> getAll(Long filmId, Integer count) {
        String query = "SELECT * FROM reviews WHERE film_id = IFNULL(?, film_id) ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(query, REVIEW_MAPPER, filmId, count);
    }

    @Override
    public Optional<Review> findById(Long id) {
        String query = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(query, REVIEW_MAPPER, id);
        if (reviews.isEmpty()) {
            log.info("Review with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Review found: userId = {}, reviewId = {}", reviews.get(0).getUserId(), id);
        return Optional.of(reviews.get(0));
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(query, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException(Review.class, "Id: " + review.getReviewId()));
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        String query = "INSERT INTO review_likes VALUES (?, ?, ?)";
        if (jdbcTemplate.update(query, id, userId, true) > 0) {
            jdbcTemplate.update(USEFUL_PLUS, id);
        }
    }

    @Override
    public void deleteLike(Long id) {
        String query = "DELETE FROM review_likes WHERE review_id = ? AND is_like = ?";
        if (jdbcTemplate.update(query, id, true) > 0) {
            jdbcTemplate.update(USEFUL_MINUS, id);
        }
    }

    @Override
    public void addDislike(Long id, Long userId) {
        String query = "INSERT INTO review_likes VALUES (?, ?, ?)";
        if (jdbcTemplate.update(query, id, userId, false) > 0) {
            jdbcTemplate.update(USEFUL_MINUS, id);
        }
    }

    @Override
    public void deleteDislike(Long id) {
        String query = "DELETE FROM review_likes WHERE review_id = ? AND is_like = ?";
        if (jdbcTemplate.update(query, id, false) > 0) {
            jdbcTemplate.update(USEFUL_PLUS, id);
        }
    }

    private Review buildReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
