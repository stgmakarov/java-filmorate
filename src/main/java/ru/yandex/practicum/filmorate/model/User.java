package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * @author Stanislav Makarov
 */
@Data
public class User {
    @NonNull
    private int id;
    @Email
    @Pattern(regexp = "^\\S*$", message = "Емейл содержит пробелы")
    private String email;
    @NonNull
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
