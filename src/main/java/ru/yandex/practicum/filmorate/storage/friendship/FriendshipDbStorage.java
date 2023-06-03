package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO user_friends VALUES (?, ?)";
        jdbcTemplate.update(query, userId, friendId);
        return friendId;
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        String query = "SELECT * FROM users \n" +
                "WHERE user_id IN (SELECT friend_id FROM user_friends WHERE user_id = ?)";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs, rowNum), userId);
    }

    @Override
    public Long deleteFriend(Long userId, Long friendId) {
        String query = "DELETE FROM user_friends WHERE friend_id = ? AND user_id = ?";
        jdbcTemplate.update(query, friendId, userId);
        return userId;
    }

    @Override
    public Boolean checkIfFriends(Long userId, Long friendId) {
        String query = "SELECT COUNT(*) as count_must_be_1 \n" +
                "FROM user_friends \n" +
                "WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.query(query, (rs, rowNum)
                -> rs.getInt("count_must_be_1"), userId, friendId).get(0) == 1;
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        String query = "SELECT * FROM users \n" +
                "WHERE user_id IN \n" +
                "(SELECT friend_id FROM user_friends WHERE user_id = ? \n" +
                "INTERSECT \n" +
                "SELECT friend_id FROM user_friends WHERE user_id = ?)";
        return jdbcTemplate.query(query, (rs, rowNum)
                -> makeUser(rs, rowNum), firstUserId, secondUserId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }


}
