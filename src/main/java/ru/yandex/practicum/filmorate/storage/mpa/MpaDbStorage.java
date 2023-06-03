package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String query = "SELECT * FROM mpa ORDER BY mpa_id ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeMpa(rs, rowNum));
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        String query = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<Mpa> mpa = jdbcTemplate.query(query, (rs, rowNum)
                -> makeMpa(rs, rowNum), id);
        if (mpa.isEmpty()) {
            log.info("Mpa with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Genre found: {} {}", mpa.get(0).getId(), mpa.get(0).getName());
        return Optional.of(mpa.get(0));
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }
}
