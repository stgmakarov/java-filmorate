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
import ru.yandex.practicum.filmorate.services.GenreService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Stanislav Makarov
 */
@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final GenreService genreService;

    @Override
    public Film create(Film film) {
        checker(film,false);

        String sqlQuery = "INSERT INTO \"Film\"\n" +
                "(\"name\", \"description\", \"releaseDate\", \"duration\", \"rating_id\")\n" +
                "VALUES(?, ?, ?, ?, ?);\n";


        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3,Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(new Mpa(film.getMpa().getId(),getRatingDescription(film.getMpa().getId())));
        //film.getGenres().forEach(genre -> film.setGenres(genreStorage.getGenre(genre.getId())));
        genreStorage.updateFilmGenres(film.getId(),film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet())
        );
        return genreService.updateGenreText(film);
    }

    @Override
    public Film update(Film film) {
        checker(film,true);

        String sqlQuery = "UPDATE \"Film\"\n" +
                "SET \"name\"=?, \"description\"=?, \"releaseDate\"=?, \"duration\"=?, \"rating_id\"=?\n" +
                "WHERE \"id\"=?;\n";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3,Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });
        film.setMpa(new Mpa(film.getMpa().getId(),getRatingDescription(film.getMpa().getId())));
        genreStorage.updateFilmGenres(film.getId(),film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()));

        return genreService.updateGenreText(film);
    }

    @Override
    public List<Film> getListOfFilms() {
        String sqlRequest = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"releaseDate\", f.\"duration\", " +
                "f.\"rating_id\", r.\"name\" AS \"rating_desc\"\n" +
                "FROM PUBLIC.\"Film\" AS f\n" +
                "LEFT JOIN \"Rating\" AS r ON f.\"rating_id\" = r.\"id\";";
        List<Film> films = jdbcTemplate.query(sqlRequest, (rs, rowNum) -> new Film(rs.getInt("id")
                , rs.getString("name")
                , rs.getString("description")
                , rs.getDate("releaseDate").toLocalDate()
                , rs.getInt("duration")
                , getLikedUsers(rs.getInt("id"))
                , new Mpa(rs.getInt("rating_id"),rs.getString("rating_desc"))
                , getFilmGenres(rs.getInt("id"))
        ));
        films.forEach(film -> film.setLikedUsers(getLikedUsers(film.getId())));
        return genreService.updateGenreText(films);
    }

    private String getRatingDescription(int ratingId) {
        String sqlQuery = "select \"name\" from \"Rating\"\n"+
                "where \"id\"=?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery,ratingId);
        if(rowSet.next()){
            return rowSet.getString("name");
        }else return " - ";
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"releaseDate\", f.\"duration\", " +
                "f.\"rating_id\", r.\"name\" AS \"rating_desc\"\n" +
                "FROM PUBLIC.\"Film\" AS f\n" +
                "LEFT JOIN \"Rating\" AS r ON f.\"rating_id\" = r.\"id\"\n" +
                "WHERE f.\"id\" =?;";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery,new String[]{String.valueOf(filmId)});
        if(rs.next()){
            return genreService.updateGenreText(new Film(rs.getInt("id")
                    , Objects.requireNonNull(rs.getString("name"))
                    , rs.getString("description")
                    , rs.getDate("releaseDate").toLocalDate()
                    , rs.getInt("duration")
                    , getLikedUsers(rs.getInt("id"))
                    , new Mpa(rs.getInt("rating_id"),rs.getString("rating_desc"))
                    , getFilmGenres(rs.getInt("id"))
            ));
        }else throw new FilmIdNotExists(filmId);
    }

    @Override
    public void checker(Film film, boolean updateFlag) {
        if(updateFlag) getFilmById(film.getId());
        if(film.getName().isEmpty())throw new EmptyFilmNameException();
        if(film.getDescription().length()>MAX_DESC_LENGTH)throw new FilmDescriptionTooMachLength(MAX_DESC_LENGTH);
        if(film.getReleaseDate().isBefore(FIRST_FILM_DATE))throw new FilmDateIsIncorrect(FIRST_FILM_DATE.format(formatter));
        if(film.getDuration()<=0)throw new FilmDurationIsIncorrect();
    }

    @Override
    public List<Film> getListOfFilms(List<Integer> films) {
        String sqlQuery = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"releaseDate\", f.\"duration\", " +
                "f.\"rating_id\", r.\"name\" AS \"rating_desc\"\n" +
                "FROM PUBLIC.\"Film\" AS f\n" +
                "LEFT JOIN \"Rating\" AS r ON f.\"rating_id\" = r.\"id\"\n" +
                "WHERE f.\"id\" IN (%s)";

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        return genreService.updateGenreText(jdbcTemplate.query(
                String.format(sqlQuery, inSql),
                films.toArray(),
                (rs, rowNum) -> new Film(rs.getInt("id")
                        , rs.getString("name")
                        , rs.getString("description")
                        , rs.getDate("releaseDate").toLocalDate()
                        , rs.getInt("duration")
                        , getLikedUsers(rs.getInt("id"))
                        , new Mpa(rs.getInt("rating_id"),rs.getString("rating_desc"))
                        , getFilmGenres(rs.getInt("id"))
                )
        ));
    }

    @Override
    public boolean like(int filmId, int userId) {
        if(getLikedUsers(filmId).contains(userId)){
            log.info("Нельзя лайкать дважды");
            return false;
        }
        String sqlQuery = "INSERT INTO \"FilmLikes\"\n" +
                "(\"film_id\", \"user_id\")\n" +
                "VALUES(?, ?);\n";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2,userId);
            return stmt;
        });

        return true;
    }

    @Override
    public boolean dislike(int filmId, int userId) {
        if(!getLikedUsers(filmId).contains(userId)){
            log.info("Убрать лайк можно только у понравившихся фильмов");
            return false;
        }
        String sqlQuery = "DELETE FROM \"FilmLikes\"\n" +
                "WHERE \"film_id\"=? AND \"user_id\"=?;\n";

        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2,userId);
            return stmt;
        });

        return true;
    }

    @Override
    public Set<Integer> getLikedUsers(int filmId) {
        String sqlQuery = "select \"user_id\" from \"FilmLikes\"\n"+
                "where \"film_id\"=?;";
        List<Integer> likedUsers = jdbcTemplate.query(sqlQuery,
                new ArgumentPreparedStatementSetter(new Object[]{filmId}),
                (rs, rowNum)-> rs.getInt("user_id")
        );
        return new HashSet<>(likedUsers);
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        String sqlQuery = "SELECT fg.\"genre_id\",g.\"description\"\n" +
                "\tFROM PUBLIC.\"FilmGenre\" AS FG\n" +
                "\tLEFT JOIN \"Genre\" AS G ON fg.\"genre_id\" = g.\"id\"\n" +
                "\tWHERE fg.\"film_id\" = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery,filmId);

        Set<Genre> res = new HashSet<>();

        while (rowSet.next()){
            res.add(new Genre(rowSet.getInt("genre_id"),rowSet.getString("description")));
        }
        return res;
    }
}
