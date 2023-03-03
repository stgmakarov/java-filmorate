package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895,12,28);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    Map<Integer, Film> filmMap = new HashMap<>();
    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        log.debug("получен запрос Post /films");
        int lastId = filmMap.size()+1;
        checker(film, false);

        Film newFilm = new Film(lastId, film.getName());
        newFilm.setDescription(film.getDescription());
        newFilm.setReleaseDate(film.getReleaseDate());
        newFilm.setDuration(film.getDuration());

        filmMap.put(lastId, newFilm);
        return newFilm;
    }

    @GetMapping
    public List<Film> getListOfFilms(){
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
            Film oldFilm = filmMap.get(filmId);
            if(!filmMap.containsKey(filmId))throw new FilmIdNotExists(filmId);
            //if(!oldFilm.getName().equals(film.getName())) throw new FilmNameIsImmutable(filmId, oldFilm.getName());
        }
        if(film.getName().isEmpty())throw new EmptyFilmNameException();
        if(film.getDescription().length()>MAX_DESC_LENGTH)throw new FilmDescriptionTooMachLength();
        if(film.getReleaseDate().isBefore(FIRST_FILM_DATE))throw new FilmDateIsIncorrect();
        if(film.getDuration()<=0)throw new FilmDurationIsIncorrect();
    }

    private static class FilmIdNotExists extends RuntimeException {
        public FilmIdNotExists(int id){
            log.error(String.format("Фильма с ID %d не существует", id));
        }

        public FilmIdNotExists(String id){
            log.error(String.format("Фильма с ID %s не существует", id));
        }
    }

    private static class FilmNameIsImmutable extends RuntimeException {
        public FilmNameIsImmutable(int id, String name){
            log.error(String.format("В базе фильм ID %d называется %s. Имя фильма менять нельзя",id,name));
        }
    }

    private static class FilmDescriptionTooMachLength extends RuntimeException {
        public FilmDescriptionTooMachLength(){
            log.error(String.format("Описание фильма не более %d символов", MAX_DESC_LENGTH));
        }
    }

    private static class FilmDateIsIncorrect extends RuntimeException {
        public FilmDateIsIncorrect(){
            log.error(String.format("Дата не может быть раньше %s", FIRST_FILM_DATE.format(formatter)));
        }
    }

    private static class FilmDurationIsIncorrect extends RuntimeException {
        public FilmDurationIsIncorrect(){
            log.error("Продолжительность фильма должна быть положительной");
        }
    }
}
