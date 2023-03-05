package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.film.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {
    private static final int MAX_DESC_LENGTH = 200;
    public static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895,12,28);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int filmLastId = 1;
    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        log.debug("получен запрос Post /films");
        int lastId = filmLastId++;
        checker(film, false);

        film.setId(lastId);
        filmMap.put(lastId, film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms(){
        return List.copyOf(filmMap.values());
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") String id){
        int filmId;
        try {
            filmId = Integer.parseInt(id);
        }catch (NumberFormatException e){
            throw new FilmIdNotExists(id);
        }

        if(!filmMap.containsKey(filmId))throw new FilmIdNotExists(filmId);
        return filmMap.get(filmId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film){
        checker(film, true);
        int filmId = film.getId();
        Film oldFilm = filmMap.get(filmId);
        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        filmMap.put(filmId,oldFilm);
        return oldFilm;
    }

    private static class EmptyFilmNameException extends RuntimeException {
        public EmptyFilmNameException(){
            log.error("Имя фильма не может быть пустым");
        }
    }

    private void checker(Film film, boolean updateFlag){
        if(updateFlag){
            int filmId = film.getId();
            if(!filmMap.containsKey(filmId))throw new FilmIdNotExists(filmId);
        }
        if(film.getName().isEmpty())throw new EmptyFilmNameException();
        if(film.getDescription().length()>MAX_DESC_LENGTH)throw new FilmDescriptionTooMachLength(MAX_DESC_LENGTH);
        if(film.getReleaseDate().isBefore(FIRST_FILM_DATE))throw new FilmDateIsIncorrect(FIRST_FILM_DATE.format(formatter));
        if(film.getDuration()<=0)throw new FilmDurationIsIncorrect();
    }
}
