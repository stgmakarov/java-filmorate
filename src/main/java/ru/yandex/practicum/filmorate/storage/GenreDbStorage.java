package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.otherException.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stanislav Makarov
 */
@Component
public class GenreDbStorage implements GenreStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenre() {
        String sqlQuery = "SELECT \"id\", \"description\"\n" +
                "FROM PUBLIC.\"Genre\";";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getInt("id"),
                rs.getString("description")));
    }

    @Override
    public Genre getGenre(int id) {
        String sqlQuery = "SELECT \"id\", \"description\"\n" +
                "FROM PUBLIC.\"Genre\" WHERE \"id\"=?;";

        return jdbcTemplate.query(sqlQuery, ps -> ps.setInt(1, id), rs -> {
            if (rs.next()) {
                return new Genre(rs.getInt("id"), rs.getString("description"));
            } else throw new GenreNotFoundException();
        });
    }

    @Override
    public String getGenreText(int id) {
        String sqlQuery = "SELECT \"description\"\n" +
                "FROM PUBLIC.\"Genre\" WHERE \"id\"=?;";

        return jdbcTemplate.query(sqlQuery, ps -> ps.setInt(1, id), rs -> {
            if (rs.next()) {
                return rs.getString("description");
            } else throw new GenreNotFoundException();
        });
    }

    @Override
    public void updateFilmGenres(int filmId, Set<Integer> genres) {
        String sqlQueryDel = "DELETE FROM \"FilmGenre\"\n" +
                "WHERE \"film_id\"=?;";

        jdbcTemplate.update(sqlQueryDel, filmId);
        if (!genres.isEmpty()) {
            String sqlQueryIns = "INSERT INTO PUBLIC.\"FilmGenre\"\n" +
                    "(\"film_id\", \"genre_id\")\n" +
                    "VALUES";
            String values = String.join(",", Collections.nCopies(genres.size(), "(?, ?)"));
            sqlQueryIns = sqlQueryIns + values;
            String finalSqlQueryIns = sqlQueryIns;
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(finalSqlQueryIns);
                AtomicInteger i = new AtomicInteger();
                genres.forEach(genre -> {
                    try {
                        statement.setInt(i.get() * 2 + 1, filmId);
                        statement.setInt((i.getAndIncrement()) * 2 + 2, genre);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                return statement;
            });
        }
    }
}
