package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping(value = "/mpa")
public class MpaController {
    @Autowired
    private MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaStorage.getMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable("id") int id) {
        return mpaStorage.getMpa(id);
    }
}
