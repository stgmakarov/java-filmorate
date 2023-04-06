package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.otherException.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

/**
 * @author Stanislav Makarov
 */
@Component
public class MpaDbStorage implements MpaStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpa() {
        String sqlQuery = "SELECT id, name " +
                "FROM RATING;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Mpa(rs.getInt("id"),
                rs.getString("name")));
    }

    @Override
    public Mpa getMpa(int id) {
        String sqlQuery = "SELECT id, name " +
                "FROM RATING WHERE id=?;";

        return jdbcTemplate.query(sqlQuery, ps -> ps.setInt(1, id), rs -> {
            if (rs.next()) {
                return new Mpa(rs.getInt("id"), rs.getString("name"));
            } else throw new MpaNotFoundException();
        });

    }
}
