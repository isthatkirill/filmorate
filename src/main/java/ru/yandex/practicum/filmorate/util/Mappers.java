package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Director;
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

    public static final RowMapper<Director> DIRECTOR_MAPPER = (ResultSet rs, int rowNum) ->
            Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("name"))
                    .build();


    public static final RowMapper<Long> LIKE_MAPPER = (ResultSet rs, int rowNum) -> rs.getLong("user_id");

    public static final RowMapper<Film> FILM_MAPPER = (ResultSet rs, int rowNum) ->
            Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration_minutes"))
                    .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                    .build();
}
