package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Long> getLikesByFilmId(Long filmId);
}
