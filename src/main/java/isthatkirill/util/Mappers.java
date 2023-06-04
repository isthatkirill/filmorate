package isthatkirill.util;

import isthatkirill.model.*;
import isthatkirill.model.enums.EventType;
import isthatkirill.model.enums.Operation;
import org.springframework.jdbc.core.RowMapper;

public class Mappers {
    public static final RowMapper<Review> REVIEW_MAPPER = (rs, rowNum) ->
            Review.builder()
                    .reviewId(rs.getLong("review_id"))
                    .content(rs.getString("content"))
                    .filmId(rs.getLong("film_id"))
                    .userId(rs.getLong("user_id"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .useful(rs.getInt("useful"))
                    .build();

    public static final RowMapper<Director> DIRECTOR_MAPPER = (rs, rowNum) ->
            Director.builder()
                    .id(rs.getLong("director_id"))
                    .name(rs.getString("name"))
                    .build();


    public static final RowMapper<Long> LIKE_MAPPER = (rs, rowNum) -> rs.getLong("user_id");

    public static final RowMapper<Long> ID_SIMILAR_MAPPER = (rs, rowNum) -> rs.getLong("user2");

    public static final RowMapper<Long> ID_FILM_MAPPER = (rs, rowNum) -> rs.getLong("film_id");

    public static final RowMapper<Film> FILM_MAPPER = (rs, rowNum) ->
            Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration_minutes"))
                    .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                    .build();

    public static final RowMapper<UserFeed> USER_FEED_MAPPER = (rs, rowNum) ->
            UserFeed.builder()
                    .eventId(rs.getLong("event_id"))
                    .userId(rs.getLong("user_id"))
                    .entityId(rs.getLong("entity_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .timestamp(rs.getLong("timestamp"))
                    .build();

    public static final RowMapper<User> USER_MAPPER = (rs, rowNum) ->
            User.builder()
                    .id(rs.getLong("user_id"))
                    .email(rs.getString("email"))
                    .name(rs.getString("name"))
                    .login(rs.getString("login"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();

    public static final RowMapper<Mpa> MPA_MAPPER = (rs, rowNum) ->
            Mpa.builder()
                    .id(rs.getInt("mpa_id"))
                    .name(rs.getString("name"))
                    .build();
}
