package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review add(Review review);

    List<Review> getAll(Long filmId, Integer count);

    Optional<Review> findById(Long id);

    Review update(Review review);

    void deleteById(Long id);

    void addLike(Long id, Long userId);

    void deleteLike(Long id);

    void addDislike(Long id, Long userId);

    void deleteDislike(Long id);
}
