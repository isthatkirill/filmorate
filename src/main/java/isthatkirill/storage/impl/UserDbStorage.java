package isthatkirill.storage.impl;

import isthatkirill.storage.UserStorage;
import isthatkirill.util.SqlQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import isthatkirill.exceptions.OnUpdateException;
import isthatkirill.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        try {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", user.getName());
            parameters.put("email", user.getEmail());
            parameters.put("login", user.getLogin());
            parameters.put("birthday", Date.valueOf(user.getBirthday()));

            Number userId = insert.executeAndReturnKey(parameters);

            user.setId(userId.longValue());
            return user;
        } catch (DataAccessException e) {
            log.warn(e.getMessage());
            throw new OnUpdateException("This user already exists");
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update(SqlQueries.DELETE_USER, id);
    }

    @Override
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs, rowNum));
    }

    @Override
    public Optional<User> findUserById(long id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(query, (rs, rowNum)
                -> makeUser(rs, rowNum), id);
        if (users.isEmpty()) {
            log.info("User with id = {} not found", id);
            return Optional.empty();
        }
        log.info("User found: {} {}", users.get(0).getId(), users.get(0).getName());
        return Optional.of(users.get(0));
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