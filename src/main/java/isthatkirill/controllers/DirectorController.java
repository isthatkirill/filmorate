package isthatkirill.controllers;

import isthatkirill.model.Director;
import isthatkirill.service.DirectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director put(@RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public Director del(@PathVariable Long id) {
        return directorService.delete(id);
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable Long id) {
        return directorService.findDirectorById(id);
    }

}
