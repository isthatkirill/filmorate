package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review addReview(Review review) {
        userService.checkUserExistent(review.getUserId());
        filmService.checkFilmExistent(review.getFilmId());
        log.info("User id = {} added new review for the film id = {}", review.getUserId(), review.getFilmId());
        return reviewStorage.add(review);
    }

    public List<Review> getAllReviewsWithGivenParams(Long filmId, Integer count) {
        List<Review> allReviews = reviewStorage.getAll(filmId, count);
        log.info("Number of reviews: {}", allReviews.size());
        return allReviews;

    }

    public Review getReviewById(Long id) {
        log.info("Get review by id = {} ", id);
        return checkReviewExistent(id);
    }

    public Review updateReview(Review review) {
        checkReviewExistent(review.getReviewId());
        log.info("Review updated: id = {}", review.getReviewId());
        return reviewStorage.update(review);
    }

    public void deleteReviewById(Long id) {
        log.info("Review deleted: id = {}", id);
        reviewStorage.deleteById(id);
    }

    public void addLikeToReview(Long id, Long userId) {
        checkReviewExistent(id);
        userService.checkUserExistent(userId);
        log.info("User id = {} liked review id = {} ", id, userId);
        reviewStorage.addLike(id, userId);
    }

    public void deleteLikeFromReview(Long id, Long userId) {
        checkReviewExistent(id);
        userService.checkUserExistent(userId);
        log.info("User id = {} deleted like from review id = {} ", id, userId);
        reviewStorage.deleteLike(id);
    }

    public void addDislikeToReview(Long id, Long userId) {
        checkReviewExistent(id);
        userService.checkUserExistent(userId);
        log.info("User id = {} disliked review id = {} ", id, userId);
        reviewStorage.addDislike(id, userId);
    }

    public void deleteDislikeFromReview(Long id, Long userId) {
        checkReviewExistent(id);
        userService.checkUserExistent(userId);
        log.info("User id = {} deleted dislike from review id = {} ", id, userId);
        reviewStorage.deleteDislike(id);
    }

    private Review checkReviewExistent(Long id) {
        return reviewStorage
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Review.class, "Id: " + id));
    }

}
