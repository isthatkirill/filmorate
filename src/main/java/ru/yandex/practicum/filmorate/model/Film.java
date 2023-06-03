package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
    @NotNull
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
