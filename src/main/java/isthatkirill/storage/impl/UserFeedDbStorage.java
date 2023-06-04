package isthatkirill.storage.impl;

import isthatkirill.model.UserFeed;
import isthatkirill.storage.UserFeedStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static isthatkirill.util.Mappers.USER_FEED_MAPPER;
import static isthatkirill.util.SqlQueries.GET_USER_FEED;

@Component
@RequiredArgsConstructor
public class UserFeedDbStorage implements UserFeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserFeed save(UserFeed userFeed) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_feed")
                .usingGeneratedKeyColumns("event_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userFeed.getUserId());
        parameters.put("entity_id", userFeed.getEntityId());
        parameters.put("event_type", userFeed.getEventType());
        parameters.put("operation", userFeed.getOperation());
        parameters.put("timestamp", userFeed.getTimestamp());

        Number eventId = insert.executeAndReturnKey(parameters);
        userFeed.setEventId(eventId.longValue());
        return userFeed;
    }

    @Override
    public List<UserFeed> getAllByUserId(Long id) {
        return jdbcTemplate.query(GET_USER_FEED, USER_FEED_MAPPER, id);
    }
}
