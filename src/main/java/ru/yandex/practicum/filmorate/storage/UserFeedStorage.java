package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.List;

public interface UserFeedStorage {
    UserFeed save(UserFeed userFeed);

    List<UserFeed> getAllByUserId(Long id);
}
