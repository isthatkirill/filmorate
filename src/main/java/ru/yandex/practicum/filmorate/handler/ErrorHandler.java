package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OnUpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundHandle(final UserNotFoundException e) {
        log.warn(e.getClass().getSimpleName() + " " + e.getMessage());
        return new ErrorResponse("User not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse filmNotFoundHandle(final FilmNotFoundException e) {
        log.warn(e.getClass().getSimpleName() + " " + e.getMessage());
        return new ErrorResponse("Film not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidDataHandle(final ValidationException e) {
        log.warn(e.getClass().getSimpleName() + " " + e.getMessage());
        return new ErrorResponse("Invalid data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidUpdateHandle(final OnUpdateException e) {
        return new ErrorResponse("Invalid data for update", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandle(final RuntimeException e) {
        log.warn(e.getClass().getSimpleName() + " " + e.getMessage());
        return new ErrorResponse("Server error", e.getMessage());
    }
}
