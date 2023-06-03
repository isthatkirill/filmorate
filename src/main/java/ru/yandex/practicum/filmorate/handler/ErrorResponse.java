package ru.yandex.practicum.filmorate.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    String error;
    String description;

}
