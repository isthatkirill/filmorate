package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewDbStorageTest {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Review firstReview;
    private Review secondReview;
    private Review thirdReview;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                .id(1L)
                .name("Test first")
                .description("Test description first")
                .duration(50)
                .releaseDate(LocalDate.of(2001, 9, 25))
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(firstFilm);

        secondFilm = Film.builder()
                .id(2L)
                .name("Test second")
                .description("Test description second")
                .duration(125)
                .releaseDate(LocalDate.of(2014, 2, 15))
                .mpa(new Mpa(2, "PG"))
                .build();
        filmStorage.addFilm(secondFilm);

        firstUser = User.builder()
                .id(1L)
                .login("TestLogin")
                .name("TestName")
                .email("testemail@email.ru")
                .birthday(LocalDate.of(2002, 2, 3))
                .build();
        userStorage.addUser(firstUser);

        secondUser = User.builder()
                .id(2L)
                .login("TestLoginTwo")
                .name("TestNameTwo")
                .email("testemailtwo@email.ru")
                .birthday(LocalDate.of(2004, 11, 23))
                .build();
        userStorage.addUser(secondUser);

        thirdUser = User.builder()
                .id(3L)
                .login("TestLoginThree")
                .name("TestNameThree")
                .email("testemailthreeo@email.ru")
                .birthday(LocalDate.of(2004, 12, 23))
                .build();
        userStorage.addUser(thirdUser);

        firstReview = Review.builder()
                .reviewId(1L)
                .content("Test positive review")
                .isPositive(true)
                .userId(firstUser.getId())
                .filmId(firstFilm.getId())
                .useful(0)
                .build();
        reviewStorage.add(firstReview);

        secondReview = Review.builder()
                .reviewId(2L)
                .content("Test negative review")
                .isPositive(false)
                .userId(firstUser.getId())
                .filmId(secondFilm.getId())
                .useful(0)
                .build();
        reviewStorage.add(secondReview);

        thirdReview = Review.builder()
                .reviewId(2L)
                .content("Test negative review 2")
                .isPositive(false)
                .userId(thirdUser.getId())
                .filmId(firstFilm.getId())
                .useful(0)
                .build();
        reviewStorage.add(thirdReview);
    }

    @Test
    public void addReviewTest() {
        List<Review> reviews = reviewStorage.getAll(null, 10);

        assertThat(reviews).hasSize(3);
        assertThat(reviews.get(0))
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "Test positive review")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void getAllReviewsByFilmIdTest() {
        List<Review> reviews = reviewStorage.getAll(2L, 10);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0))
                .hasFieldOrPropertyWithValue("reviewId", 2L)
                .hasFieldOrPropertyWithValue("content", "Test negative review")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("filmId", 2L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void getAllReviewsWithCountParamTest() {
        List<Review> reviews = reviewStorage.getAll(null, 1);

        assertThat(reviews).hasSize(1);
    }

    @Test
    public void getAllReviewsByFilmIdWithCountParamTest() {
        List<Review> reviews = reviewStorage.getAll(1L, 1);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0))
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "Test positive review")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void findReviewByIdTest() {
        Optional<Review> reviewOptional = reviewStorage.findById(3L);

        assertThat(reviewOptional)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("reviewId", 3L)
                .hasFieldOrPropertyWithValue("content", "Test negative review 2")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 3L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void findNonExistentReviewByIdTest() {
        Optional<Review> reviewOptional = reviewStorage.findById(-999999L);

        assertThat(reviewOptional).isNotPresent();
    }


    @Test
    public void updateReviewTest() {
        Review updatedReview = Review.builder()
                .reviewId(3L)
                .content("Now thirdReview is positive")
                .isPositive(true)
                .filmId(1L)
                .userId(3L)
                .useful(0)
                .build();
        reviewStorage.update(updatedReview);

        assertThat(reviewStorage.findById(3L))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("reviewId", 3L)
                .hasFieldOrPropertyWithValue("content", "Now thirdReview is positive")
                .hasFieldOrPropertyWithValue("isPositive", true)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("userId", 3L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void deleteReviewByIdTest() {
        reviewStorage.deleteById(1L);

        assertThat(reviewStorage.getAll(null, 10)).hasSize(2);

        reviewStorage.deleteById(2L);

        assertThat(reviewStorage.getAll(null, 10)).hasSize(1);
        assertThat(reviewStorage.findById(3L)).isPresent();
        assertThat(reviewStorage.findById(1L)).isNotPresent();
    }

    @Test
    public void addLikeToReviewTest() {
        reviewStorage.addLike(1L, 2L);
        reviewStorage.addLike(1L, 3L);

        assertThat(reviewStorage.findById(1L))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("useful", 2);
    }


    @Test
    public void deleteLikeFromReviewTest() {
        addLikeToReviewTest();
        reviewStorage.deleteLike(1L);

        assertThat(reviewStorage.findById(1L))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("useful", 1);
    }

    @Test
    public void addDislikeToReviewTest() {
        reviewStorage.addLike(1L, 2L);
        reviewStorage.addDislike(1L, 3L);

        assertThat(reviewStorage.findById(1L))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void deleteDislikeFromReviewTest() {
        addDislikeToReviewTest();
        reviewStorage.deleteDislike(1L);

        assertThat(reviewStorage.findById(1L))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("useful", 1);
    }

    @Test
    public void reviewsShouldBeSortedDescByUsefulTest() {
        reviewStorage.addLike(3L, 2L);
        reviewStorage.addDislike(1L, 2L);

        assertThat(reviewStorage.getAll(null, 10)
                .stream()
                .map(Review::getReviewId))
                .containsExactly(3L, 2L, 1L);
    }

}