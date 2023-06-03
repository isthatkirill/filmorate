package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.Mappers.REVIEW_MAPPER;
import static ru.yandex.practicum.filmorate.util.SqlQueries.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

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
        return jdbcTemplate.query(FIND_ALL_REVIEWS, REVIEW_MAPPER, filmId, count);
    }

    @Override
    public Optional<Review> findById(Long id) {
        List<Review> reviews = jdbcTemplate.query(FIND_REVIEW_BY_ID, REVIEW_MAPPER, id);
        if (reviews.isEmpty()) {
            log.info("Review with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Review found: userId = {}, reviewId = {}", reviews.get(0).getUserId(), id);
        return Optional.of(reviews.get(0));
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException(Review.class, "Id: " + review.getReviewId()));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_REVIEW, id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        try {
            if (jdbcTemplate.update(ADD_LIKE_OR_DISLIKE_REVIEW, id, userId, true) > 0) {
                jdbcTemplate.update(USEFUL_PLUS, id);
            }
        } catch (DataAccessException ignored) {

        }
    }

    @Override
    public void deleteLike(Long id) {
        if (jdbcTemplate.update(DELETE_LIKE_OR_DISLIKE_REVIEW, id, true) > 0) {
            jdbcTemplate.update(USEFUL_MINUS, id);
        }
    }

    @Override
    public void addDislike(Long id, Long userId) {
        try {
            if (jdbcTemplate.update(ADD_LIKE_OR_DISLIKE_REVIEW, id, userId, false) > 0) {
                jdbcTemplate.update(USEFUL_MINUS, id);
            }
        } catch (DataAccessException ignored) {

        }
    }

    @Override
    public void deleteDislike(Long id) {
        if (jdbcTemplate.update(DELETE_LIKE_OR_DISLIKE_REVIEW, id, false) > 0) {
            jdbcTemplate.update(USEFUL_PLUS, id);
        }
    }

}
