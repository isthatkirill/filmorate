package isthatkirill.storage.impl;

import isthatkirill.model.User;
import isthatkirill.storage.FriendshipStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static isthatkirill.util.Mappers.USER_MAPPER;
import static isthatkirill.util.SqlQueries.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        return friendId;
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        return jdbcTemplate.query(GET_FRIENDS_BY_USER_ID, USER_MAPPER, userId);
    }

    @Override
    public Long deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, friendId, userId);
        return userId;
    }

    @Override
    public Boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        return jdbcTemplate.query(CHECK_IF_FRIENDS, (rs, rowNum)
                -> rs.getInt("count_must_be_1"), userId, friendId).get(0) == 1;
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return jdbcTemplate.query(COMMON_FRIENDS, USER_MAPPER, firstUserId, secondUserId);
    }


}
