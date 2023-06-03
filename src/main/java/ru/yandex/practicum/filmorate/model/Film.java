package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private final Set<Long> likes = new HashSet<>();

    public void addLike(@Positive Long id) {
        likes.add(id);
    }

    public boolean deleteLike(@Positive Long id) {
        return likes.remove(id);
    }

    public List<Long> getLikes() {
        return new ArrayList<>(likes);
    }
}
