package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * @author Stanislav Makarov
 */
public interface MpaStorage {
    List<Mpa> getMpa();
    Mpa getMpa(int id);
}
