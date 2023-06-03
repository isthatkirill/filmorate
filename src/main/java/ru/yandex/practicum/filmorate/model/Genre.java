package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class Genre {

    private Integer id;

    @EqualsAndHashCode.Exclude
    private String name;
}
