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
import ru.yandex.practicum.filmorate.exceptions.user.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Stanislav Makarov
 */
@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        checker(user, false);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        String sqlQuery = "insert into \"User\"(\"email\", \"login\", \"name\", \"birthday\") " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        setFriends(user);
        return user;
    }

    @Override
    public User update(User user) {
        checker(user, true);
        String sqlQuery = "UPDATE \"User\"\n" +
                "SET \"email\"=?, \"login\"=?, \"name\"=?, \"birthday\"=?\n" +
                "WHERE \"id\"=?;";

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        user.setEmail(user.getEmail());

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        setFriends(user);
        return user;
    }

    @Override
    public void checker(User user, boolean updateFlag) {
        String email = user.getEmail().toLowerCase();
        String login = user.getLogin().toLowerCase();
        OptionalInt currId = OptionalInt.empty();
        if (updateFlag) {
            currId = OptionalInt.of(user.getId());
            getUserById(user.getId());
        }
        //+email уже существует
        if (checkEmailExists(email, currId)) throw new EmailAlreadyRegistered(email);
        //+логин уже занят
        if (checkLoginExists(login, currId)) throw new LoginAlreadyRegistered(login);

        if (email.isEmpty()) throw new EmailWrong(email, "электронная почта не может быть пустой");
        if (!email.contains("@")) throw new EmailWrong(email, "электронная почта должна содержать символ @");
        if (email.contains(" ")) throw new EmailWrong(email, "электронная почта не должна содержать пробелы");
        if (login.isEmpty()) throw new LoginWrong(login, "логин не может быть пустым");
        if (login.contains(" ")) throw new LoginWrong(login, "логин не может содержать пробелы");
        if (!user.getBirthday().isBefore(LocalDate.now())) throw new BirthDayDateWrong();
    }

    private boolean checkEmailExists(String email, OptionalInt currId) {
        SqlRowSet sqlRowSet;
        if (currId.isEmpty()) {
            String sqlQuery = "select count(*) from \"User\"\n" +
                    "where \"email\"=?;";
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, email);
        } else {
            String sqlQuery = "select count(*) from \"User\"\n" +
                    "where \"email\"=? and \"id\" !=?;";
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, email, currId.getAsInt());
        }
        int cnt = 0;
        if (sqlRowSet.next()) {
            cnt = sqlRowSet.getInt(1);
        }
        return cnt > 0;
    }

    private boolean checkLoginExists(String login, OptionalInt currId) {
        SqlRowSet sqlRowSet;
        if (currId.isEmpty()) {
            String sqlQuery = "select count(*) from \"User\"\n" +
                    "where \"login\"=?;";
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, login);
        } else {
            String sqlQuery = "select count(*) from \"User\"\n" +
                    "where \"login\"=? and \"id\" !=?;";
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, login, currId.getAsInt());
        }
        int cnt = 0;
        if (sqlRowSet.next()) {
            cnt = sqlRowSet.getInt(1);
        }
        return cnt > 0;
    }

    @Override
    public List<User> getListOfUsers() {
        String sqlQuery = "select * from \"User\";";

        return jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new User(rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriends(rs.getInt("id"), true),
                        getFriends(rs.getInt("id"), false)));
    }

    @Override
    public List<User> getListOfUsers(List<Integer> users) {
        String sqlQuery = "SELECT * FROM \"User\" WHERE \"id\" IN (%s)";

        String inSql = String.join(",", Collections.nCopies(users.size(), "?"));

        return jdbcTemplate.query(
                String.format(sqlQuery, inSql),
                users.toArray(),
                (rs, rowNum) -> new User(rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriends(rs.getInt("id"), true),
                        getFriends(rs.getInt("id"), false)));
    }

    @Override
    public User getUserById(int userId) {
        List<User> oneUser = getListOfUsers(List.of(userId));
        if (!oneUser.isEmpty()) {
            return oneUser.get(0);
        } else throw new UserIdNotExists(userId);
    }

    @Override
    public boolean addFriend(int userId, int friendId, boolean confirmed) {
        if (getFriends(userId).contains(friendId)) return false;

        String sqlQuery = "INSERT INTO \"Friends\"\n" +
                "(\"user_id\", \"friend_id\", \"confirmed\")\n" +
                "VALUES(?, ?, ?);";

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery);
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.setBoolean(3, confirmed);
            return statement;
        });

        return true;
    }

    @Override
    public boolean removeFriend(int userId, int friendId) {
        if (!getFriends(userId).contains(friendId)) return false;

        String sqlQuery = "DELETE FROM \"Friends\"\n" +
                "WHERE \"user_id\"=? AND \"friend_id\"=?;";

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery);
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            return statement;
        });

        return true;
    }

    @Override
    public boolean confirmFriend(int userId, int friendId) {
        if (getFriends(userId, false).contains(friendId)) {
            String sqlRequest = "UPDATE \"Friends\"\n" +
                    "SET \"confirmed\"=true\n" +
                    "WHERE \"user_id\"=? AND \"friend_id\"=?;\n";
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sqlRequest);
                statement.setInt(1, userId);
                statement.setInt(2, friendId);
                return statement;
            });
            return true;
        } else return false;
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return getFriends(userId, null);
    }

    @Override
    public Set<Integer> getFriends(int userId, Boolean confirmed) {
        String sqlQuery;
        if (confirmed != null) {
            sqlQuery = String.format("select \"friend_id\" from \"Friends\"\n" +
                    "where \"user_id\"=? and \"confirmed\" = %b;", confirmed);
        } else {
            sqlQuery = "select \"friend_id\" from \"Friends\"\n" +
                    "where \"user_id\"=?;";
        }

        List<Integer> friends = jdbcTemplate.query(sqlQuery,
                new ArgumentPreparedStatementSetter(new Object[]{userId}),
                (rs, rowNum) -> rs.getInt("friend_id")
        );
        return new HashSet<>(friends);
    }

    private void setFriends(User user) {
        Set<Integer> conf = getFriends(user.getId(), true);
        Set<Integer> unconf = getFriends(user.getId(), false);
        user.setFriends(conf);
        user.setUnconfirmedFriends(unconf);
    }
}
