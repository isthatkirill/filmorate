package isthatkirill.storage;

import isthatkirill.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<Mpa> getAllMpa();

    Optional<Mpa> findMpaById(int id);

}
