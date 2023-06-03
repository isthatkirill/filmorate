package isthatkirill.service;

import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.storage.MpaStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import isthatkirill.model.Mpa;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        log.info("Get all mpa");
        return mpaStorage.getAllMpa();
    }

    public Mpa findMpaById(Integer id) {
        log.info("Get mpa by id = {}", id);
        return checkMpaExistent(id);
    }

    private Mpa checkMpaExistent(Integer id) {
        return mpaStorage
                .findMpaById(id)
                .orElseThrow(() -> new EntityNotFoundException(Mpa.class, "Id: " + id));
    }
}
