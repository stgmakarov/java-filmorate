package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping(value = "/genres")
public class GenreController {
    @Autowired
    private GenreStorage genreStorage;

    @GetMapping
    public List<Genre> getAllGenre() {
       return genreStorage.getGenre();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@Valid @PathVariable("id") int id) {
        return genreStorage.getGenre(id);
    }
}
