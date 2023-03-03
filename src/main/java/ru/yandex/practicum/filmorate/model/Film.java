package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

/**
 * @author Stanislav Makarov
 */
@Data
public class Film {
    private final int id;
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    @PositiveOrZero
    private int duration;
}
