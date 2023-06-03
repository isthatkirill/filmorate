package ru.yandex.practicum.filmorate.util;

public class SqlQueries {
    public static final String FIND_POPULAR_FILMS = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";

    public static final String FIND_POPULAR_FILMS_BY_GENRE_ID_AND_YEAR = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "JOIN film_genres AS fg ON fg.film_id = f.film_id " +
            "WHERE genre_id = ?" +
            "AND YEAR(f.release_date) = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";

    public static final String FIND_POPULAR_FILMS_BY_YEAR = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE year(f.release_date) = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";
    public static final String FIND_POPULAR_FILMS_BY_GENRE_ID = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "JOIN film_genres AS fg ON fg.film_id = f.film_id " +
            "WHERE genre_id = ?" +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";

    public static final String FIND_FILM_BY_ID = "SELECT films.*, mpa.name AS mpa_name " +
            "FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id = ?";

    public static final String FIND_ALL_FILMS = "SELECT films.*, mpa.name AS mpa_name " +
            "FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";

    public static final String UPDATE_FILM_BY_ID = "UPDATE FILMS set name = ?, description = ?, release_date = ?, " +
            "duration_minutes = ?, mpa_id = ? WHERE film_id = ?";

    public static final String GET_FILMS_BY_LIST_IDS = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE f.film_id IN (%s) " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC";

    public static final String GET_COMMON_FILMS_ID = "SELECT film_id FROM films_likes WHERE user_id = ? " +
            "INTERSECT " +
            "SELECT film_id FROM films_likes  WHERE user_id = ?";

    public static final String GET_DIFFERENT_FILMS_ID = "SELECT film_id FROM films_likes WHERE user_id = ? " +
            "EXCEPT " +
            "SELECT film_id FROM films_likes  WHERE user_id = ?";

    public static final String FIND_FILMS_BY_DIRECTOR_ID = "SELECT films.*, mpa.name AS mpa_name " +
            "FROM films LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id IN " +
            "(SELECT film_id FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?)";

    public static final String SEARCH_FILMS_BY_TITLE = "SELECT f.*,\n" +
            "COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
            "FROM films as f\n" +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
            "WHERE f.name ILIKE '%' || ? || '%'\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY total_likes DESC\n";

    public static final String SEARCH_FILMS_BY_DIRECTOR = "SELECT f.*,\n" +
            " COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
            "FROM films as f\n" +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
            "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE d.name ILIKE '%' || ? || '%'\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY total_likes DESC\n";

    public static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR = "SELECT f.*,\n" +
            " COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
            "FROM films as f\n" +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
            "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE d.name ILIKE '%' || ? || '%'\n" +
            "OR f.name ILIKE '%' || ? || '%'\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY total_likes DESC\n";

    public static final String GET_SIMILAR_USER = "SELECT DISTINCT user2 FROM (" +
            "SELECT t1.user_id AS user1, t2.user_id AS user2, " +
            "ROW_NUMBER() OVER (PARTITION BY t1.user_id ORDER BY count(t2.film_id) desc) AS row_num " +
            "FROM films_likes t1 " +
            "JOIN films_likes t2 ON t1.film_id = t2.film_id " +
            "JOIN films_likes t3 ON t1.user_id = t3.user_id " +
            "WHERE t2.user_id != ? " +
            "GROUP BY t1.user_id, t2.user_id) " +
            "WHERE row_num = 1";

    public static final String FIND_MATCH = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films AS f LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE (f.name=?) " +
            "AND (f.release_date=?) AND (f.duration_minutes=?)";
    public static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";

    public static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";

    public static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";

    public static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";

    public static final String SAVE_FILM_DIRECTORS = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";

    public static final String ADD_DIRECTOR_FOR_FILM_BY_ID = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";

    public static final String DELETE_DIRECTORS_BY_FILM_ID = "DELETE FROM film_directors WHERE film_id = ?";

    public static final String SAVE_DIRECTOR = "INSERT INTO directors (name) VALUES (?)";

    public static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE director_id = ?";

    public static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";

    public static final String GET_DIRECTOR_BY_FILM_ID = "SELECT d.director_id, d.name FROM film_directors as fd\n" +
            "LEFT JOIN directors AS d on d.director_id = fd.director_id\n" +
            "WHERE FILM_ID = ?\n" +
            "ORDER BY d.director_id ASC";
    public static final String GET_USER_FEED = "SELECT event_id, user_id, entity_id, event_type, operation, timestamp " +
            "FROM user_feed WHERE user_id IN (?)";

    public static final String USEFUL_PLUS = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";

    public static final String USEFUL_MINUS = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

    public static final String FIND_ALL_REVIEWS = "SELECT * FROM reviews WHERE film_id = IFNULL(?, film_id) ORDER BY useful DESC LIMIT ?";

    public static final String FIND_REVIEW_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";

    public static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";

    public static final String DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";

    public static final String ADD_LIKE_OR_DISLIKE_REVIEW = "INSERT INTO review_likes VALUES (?, ?, ?)";

    public static final String DELETE_LIKE_OR_DISLIKE_REVIEW = "DELETE FROM review_likes WHERE review_id = ? AND is_like = ?";

}
