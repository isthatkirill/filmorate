package ru.yandex.practicum.filmorate.exceptions;

public class UserFeedFieldNotFoundException extends RuntimeException {

    public UserFeedFieldNotFoundException(String message) {
        super(message);
    }
}
