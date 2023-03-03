package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

/**
 * @author Stanislav Makarov
 */
@Data
public class User {
    @PositiveOrZero
    final private int id;
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
