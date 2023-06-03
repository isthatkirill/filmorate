package isthatkirill.service;

import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.model.Film;
import isthatkirill.storage.FilmStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import isthatkirill.model.Director;
import isthatkirill.storage.DirectorStorage;

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

    public Director findDirectorById(Integer id) {
        log.info("Get director by id = {}", id);
        return checkDirectorExistent(id);
    }

    public Director create(Director director) {
        return directorStorage.save(director);
    }

    public Director update(Director director) {
        int id =  (int) director.getId();
        if (directorStorage.get(id).isPresent()) {
            directorStorage.update(director);
            log.info("Updated director {}", director.getId());
            return director;
        } else {
            log.error("There is no such director in our list of directors");
            throw new EntityNotFoundException(Film.class,"There is no such director in our list of directors");
        }
    }

    public Director delete(Integer id) {
        Optional<Director> director = directorStorage.get(id);
        if (director.isPresent()) {
            List<Film> filmsDirector = new ArrayList<>(filmStorage.findFilmListDirectorById(id));
            if (!filmsDirector.isEmpty()) {
                for (Film film : filmsDirector) {
                    directorStorage.deleteDirectorsByFilmId(film.getId());
                    film.getDirectors().remove(director);
                }
            }
            directorStorage.delete(director.get());
            log.info("Deleted director {}", id);
            return director.get();
        } else {
            log.error("There is no such director {} in our list of directors", id);
            throw new EntityNotFoundException(Film.class, "There is no such director " + id + " in our list of directors");
        }
    }

    private Director checkDirectorExistent(Integer id) {
        return directorStorage
                .get(id)
                .orElseThrow(() -> new EntityNotFoundException(Director.class, "Id: " + id));
    }
}
