package isthatkirill.service;

import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.model.Director;
import isthatkirill.model.Film;
import isthatkirill.storage.DirectorStorage;
import isthatkirill.storage.FilmStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;
    private final FilmStorage filmStorage;

    public List<Director> findAll() {
        log.info("All list Directors");
        return directorStorage.getAll();
    }

    public Director findDirectorById(Long id) {
        log.info("Get director by id = {}", id);
        return checkDirectorExistent(id);
    }

    public Director create(Director director) {
        return directorStorage.save(director);
    }

    public Director update(Director director) {
        Long id = director.getId();
        if (directorStorage.get(id).isPresent()) {
            directorStorage.update(director);
            log.info("Updated director {}", director.getId());
            return director;
        } else {
            throw new EntityNotFoundException(Film.class, "There is no such director in our list of directors");
        }
    }

    public Director delete(Long id) {
        Optional<Director> director = directorStorage.get(id);
        if (director.isPresent()) {
            List<Film> filmsDirector = new ArrayList<>(filmStorage.findFilmListDirectorById(id));
            filmsDirector.forEach(film -> {
                directorStorage.deleteDirectorsByFilmId(film.getId());
                film.getDirectors().remove(director);
            });
            directorStorage.delete(director.get());
            log.info("Deleted director {}", id);
            return director.get();
        } else {
            throw new EntityNotFoundException(Film.class, "There is no such director " + id + " in our list of directors");
        }
    }

    private Director checkDirectorExistent(Long id) {
        return directorStorage
                .get(id)
                .orElseThrow(() -> new EntityNotFoundException(Director.class, "Id: " + id));
    }
}
