package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;

public class Mappers {
    public static final RowMapper<Review> REVIEW_MAPPER = (ResultSet rs, int rowNum) ->
            Review.builder()
                    .reviewId(rs.getLong("review_id"))
                    .content(rs.getString("content"))
                    .filmId(rs.getLong("film_id"))
                    .userId(rs.getLong("user_id"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .useful(rs.getInt("useful"))
                    .build();
}
