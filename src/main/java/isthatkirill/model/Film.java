package isthatkirill.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import isthatkirill.util.ReleaseDate;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @Positive
    Long id;
    @NotBlank
    String name;
    @NotNull
    @Size(max = 200)
    String description;
    @ReleaseDate
    LocalDate releaseDate;
    @NotNull
    @Positive
    Integer duration;
    @NotNull
    Mpa mpa;
    final Set<Genre> genres = new HashSet<>();
    final Set<Director> directors = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        directors.add(director);
    }
}
