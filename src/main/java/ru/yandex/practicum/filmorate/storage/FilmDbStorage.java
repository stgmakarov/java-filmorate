package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.film.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * @author Stanislav Makarov
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        checker(film, false);

        String sqlQuery = "INSERT INTO FILM " +
                "(name, description, releaseDate, duration, rating_id) " +
                "VALUES(?, ?, ?, ?, ?); ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(new Mpa(film.getMpa().getId(), getRatingDescription(film.getMpa().getId())));

/*        genreService.updateFilmGenres(film);
        return genreService.updateGenreText(film);*/
        return film;
    }

    @Override
    public Film update(Film film) {
        checker(film, true);

        String sqlQuery = "UPDATE FILM " +
                "SET name=?, description=?, releaseDate=?, duration=?, rating_id=? " +
                "WHERE id=?";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });
        film.setMpa(new Mpa(film.getMpa().getId(), getRatingDescription(film.getMpa().getId())));

/*        genreService.updateFilmGenres(film);
        return genreService.updateGenreText(film);*/
        return film;
    }

    @Override
    public List<Film> getListOfFilms() {
        String sqlRequest = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.name AS rating_desc " +
                "FROM FILM AS f " +
                "LEFT JOIN RATING AS r ON f.rating_id = r.id;";
        List<Film> films = jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Film(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                getLikedUsers(rs.getInt("id")),
                new Mpa(rs.getInt("rating_id"), rs.getString("rating_desc")),
                getFilmGenres(rs.getInt("id"))
        ));
        films.forEach(film -> film.setLikedUsers(getLikedUsers(film.getId())));
        /*return genreService.updateGenreText(films);*/
        return films;
    }

    private String getRatingDescription(int ratingId) {
        String sqlQuery = "select name from RATING " +
                "where id=?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, ratingId);
        if (rowSet.next()) {
            return rowSet.getString("name");
        } else return " - ";
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.name AS RATING_DESC " +
                "FROM FILM AS f " +
                "LEFT JOIN RATING AS r ON f.rating_id = r.id " +
                "WHERE f.id =?;";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, new String[]{String.valueOf(filmId)});
        if (rs.next()) {
            return new Film(rs.getInt("id"),
                    Objects.requireNonNull(rs.getString("name")),
                    rs.getString("description"),
                    rs.getDate("releaseDate").toLocalDate(),
                    rs.getInt("duration"),
                    getLikedUsers(rs.getInt("id")),
                    new Mpa(rs.getInt("rating_id"), rs.getString("RATING_DESC")),
                    getFilmGenres(rs.getInt("id"))
            );
        } else throw new FilmIdNotExists(filmId);
    }

    @Override
    public void checker(Film film, boolean updateFlag) {
        if (updateFlag) getFilmById(film.getId());
        if (film.getName().isEmpty()) throw new EmptyFilmNameException();
        if (film.getDescription().length() > MAX_DESC_LENGTH) throw new FilmDescriptionTooMachLength(MAX_DESC_LENGTH);
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new FilmDateIsIncorrect(FIRST_FILM_DATE.format(formatter));
        if (film.getDuration() <= 0) throw new FilmDurationIsIncorrect();
    }

    @Override
    public List<Film> getListOfFilms(List<Integer> films) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.rating_id, r.name AS rating_desc " +
                "FROM FILM AS f " +
                "LEFT JOIN RATING AS r ON f.rating_id = r.id " +
                "WHERE f.id IN (%s)";

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        return jdbcTemplate.query(
                String.format(sqlQuery, inSql),
                films.toArray(),
                (rs, rowNum) -> new Film(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration"),
                        getLikedUsers(rs.getInt("id")),
                        new Mpa(rs.getInt("rating_id"), rs.getString("rating_desc")),
                        getFilmGenres(rs.getInt("id"))
                )
        );
    }

    @Override
    public boolean like(int filmId, int userId) {
        if (getLikedUsers(filmId).contains(userId)) {
            log.info("Нельзя лайкать дважды");
            return false;
        }
        String sqlQuery = "INSERT INTO FILMLIKES " +
                "(film_id, user_id) " +
                "VALUES(?, ?); ";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2, userId);
            return stmt;
        });

        return true;
    }

    @Override
    public boolean dislike(int filmId, int userId) {
        if (!getLikedUsers(filmId).contains(userId)) {
            log.info("Убрать лайк можно только у понравившихся фильмов");
            return false;
        }
        String sqlQuery = "DELETE FROM FILMLIKES " +
                "WHERE film_id=? AND user_id=?; ";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2, userId);
            return stmt;
        });

        return true;
    }

    @Override
    public Set<Integer> getLikedUsers(int filmId) {
        String sqlQuery = "select user_id from FILMLIKES " +
                "where film_id=?;";
        List<Integer> likedUsers = jdbcTemplate.query(sqlQuery,
                new ArgumentPreparedStatementSetter(new Object[]{filmId}),
                (rs, rowNum) -> rs.getInt("user_id")
        );
        return new HashSet<>(likedUsers);
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        String sqlQuery = "SELECT fg.genre_id,g.description " +
                "FROM FilmGenre AS FG " +
                "LEFT JOIN Genre AS G ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        Set<Genre> res = new HashSet<>();

        while (rowSet.next()) {
            res.add(new Genre(rowSet.getInt("genre_id"), rowSet.getString("description")));
        }
        return res;
    }
}
