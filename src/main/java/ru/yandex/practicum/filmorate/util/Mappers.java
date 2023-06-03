package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

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

    public static final RowMapper<Director> DIRECTOR_MAPPER = (ResultSet rs, int rowNum) ->
            Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("name"))
                    .build();


    public static final RowMapper<Long> LIKE_MAPPER = (ResultSet rs, int rowNum) -> rs.getLong("user_id");

    public static final RowMapper<Long> ID_SIMILAR_MAPPER = (rs, rowNum) -> rs.getLong("user2");

    public static final RowMapper<Long> ID_FILM_MAPPER = (rs, rowNum) -> rs.getLong("film_id");

    public static final RowMapper<Film> FILM_MAPPER = (ResultSet rs, int rowNum) ->
            Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration_minutes"))
                    .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                    .build();

    public static final RowMapper<UserFeed> USER_FEED_MAPPER = (ResultSet rs, int rowNum) ->
            UserFeed.builder()
                    .eventId(rs.getLong("event_id"))
                    .userId(rs.getLong("user_id"))
                    .entityId(rs.getLong("entity_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .timestamp(rs.getLong("timestamp"))
                    .build();
}
