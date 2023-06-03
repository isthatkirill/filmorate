package ru.yandex.practicum.filmorate.storage;

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
            "WHERE f.film_id IN (" +
            "SELECT film_id FROM film_genres " +
            "WHERE genre_id = ?) AND " +
            "YEAR(f.release_date) = ? " +
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
            "WHERE f.film_id IN (" +
            "SELECT film_id FROM film_genres " +
            "WHERE genre_id = ?) " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";

    public static final String FIND_FILM_BY_ID = "SELECT films.*, mpa.name AS mpa_name " +
            "FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id = ?";

    public static final String FIND_ALL_FILMS = "SELECT films.*, mpa.name AS mpa_name " +
            "FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";

    public static final String UPDATE_FILM_BY_ID = "UPDATE FILMS set name = ?, description = ?, release_date = ?, " +
            "duration_minutes = ?, mpa_id = ? WHERE film_id = ?";

    public static final String GET_COMMON_FILMS_BY_USERS = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE f.film_id IN (SELECT film_id FROM films_likes WHERE user_id = ? " +
            "INTERSECT " +
            "SELECT film_id FROM films_likes  WHERE user_id = ?) " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC";

    public static final String GET_RECOMMENDATIONS = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films as f " +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE f.film_id IN (SELECT film_id FROM films_likes WHERE user_id = ? " +
            "EXCEPT " +
            "SELECT film_id FROM films_likes  WHERE user_id = ?) " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC";

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
            "WHERE f.film_id IN (SELECT fd.film_id\n" +
            "FROM film_directors AS fd WHERE director_id IN (SELECT director_id\n" +
            "FROM directors AS d\n" +
            "WHERE d.name ILIKE '%' || ? || '%'))\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY total_likes DESC\n";

    public static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR = "SELECT f.*,\n" +
            " COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
            "FROM films as f\n" +
            "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
            "WHERE f.film_id IN (SELECT fd.film_id\n" +
            "FROM film_directors AS fd WHERE director_id IN (SELECT director_id\n" +
            "FROM directors AS d\n" +
            "WHERE d.name ILIKE '%' || ? || '%'))\n" +
            "OR f.name ILIKE '%' || ? || '%'\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY total_likes DESC\n";

    public static final String GET_SIMILAR_USER = "SELECT user2 FROM (" +
            "SELECT t1.user_id AS user1, t2.user_id AS user2, " +
            "ROW_NUMBER() OVER (PARTITION BY t1.user_id ORDER BY count(t2.film_id) desc) AS row_num " +
            "FROM films_likes t1 " +
            "JOIN films_likes t2 ON t1.film_id = t2.film_id " +
            "WHERE t1.user_id = ? AND t2.user_id IN (SELECT DISTINCT user_id FROM films_likes WHERE user_id != ?) " +
            "GROUP BY t1.user_id, t2.user_id) " +
            "WHERE row_num = 1";

    public static final String FIND_MATCH = "SELECT f.*, mpa.name AS mpa_name " +
            "FROM films AS f LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE (f.name=?) " +
            "AND (f.release_date=?) AND (f.duration_minutes=?)";
    public static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";

    public static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
}
