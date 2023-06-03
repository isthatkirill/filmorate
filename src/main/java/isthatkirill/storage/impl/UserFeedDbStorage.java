package isthatkirill.storage.impl;

import isthatkirill.model.UserFeed;
import isthatkirill.storage.UserFeedStorage;
import isthatkirill.util.Mappers;
import isthatkirill.util.SqlQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return jdbcTemplate.query(SqlQueries.GET_USER_FEED, Mappers.USER_FEED_MAPPER, id);
    }
}
