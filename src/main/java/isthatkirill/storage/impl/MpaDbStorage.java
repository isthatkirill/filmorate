package isthatkirill.storage.impl;

import isthatkirill.model.Mpa;
import isthatkirill.storage.MpaStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static isthatkirill.util.Mappers.MPA_MAPPER;
import static isthatkirill.util.SqlQueries.FIND_MPA_BY_ID;
import static isthatkirill.util.SqlQueries.GET_ALL_MPA;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(GET_ALL_MPA, MPA_MAPPER);
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        List<Mpa> mpa = jdbcTemplate.query(FIND_MPA_BY_ID, MPA_MAPPER, id);
        if (mpa.isEmpty()) {
            log.info("Mpa with id = {} not found", id);
            return Optional.empty();
        }
        log.info("MPA found: {} {}", mpa.get(0).getId(), mpa.get(0).getName());
        return Optional.of(mpa.get(0));
    }

}
