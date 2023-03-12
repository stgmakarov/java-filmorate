package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Stanislav Makarov
 */
@Data
public class Film {
    @NonNull
    private int id;
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    @PositiveOrZero
    private int duration;
    private Set<Integer> likedUsers = new HashSet<>();
}
