package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Stanislav Makarov
 */
@Service
@Slf4j
public class FilmService {
    private final Map<Integer, FilmLikes> filmLikesMap = new HashMap<>();

    public List<Integer> getTop(int topElements){
        return filmLikesMap.values().stream()
                .sorted(Comparator.comparingInt(FilmLikes::getLikesCount).reversed())
                .limit(topElements)
                .map(filmLikes -> filmLikes.filmId)
                .collect(Collectors.toList());
    }

    public List<FilmLikes> getAllFilmLikesInfo(){
        return List.copyOf(filmLikesMap.values());
    }

    public void filmInit(int filmId){
        FilmLikes filmLikes = new FilmLikes(filmId);
        if(filmLikesMap.containsKey(filmId)) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ИД нового фильма уже занят");
        filmLikesMap.put(filmId,filmLikes);
    }

    public boolean like(int filmId, int userId){
        FilmLikes filmLikes;
        if(!filmLikesMap.containsKey(filmId)) filmLikes = new FilmLikes(filmId);
        else filmLikes = filmLikesMap.get(filmId);

        boolean res = filmLikes.like(userId);
        filmLikesMap.put(filmId, filmLikes);

        return res;
    }

    public int getLikesCount(int filmId){
        if(!filmLikesMap.containsKey(filmId)) return 0;
        return filmLikesMap.get(filmId).getLikesCount();
    }

    public boolean dislike(int filmId, int userId){
        FilmLikes filmLikes;
        if(!filmLikesMap.containsKey(filmId)){
            log.info("Фильму ещё не поставили лайки");
            return false;
        }

        filmLikes = filmLikesMap.get(filmId);

        boolean res = filmLikes.dislike(userId);
        filmLikesMap.put(filmId, filmLikes);

        return res;
    }

    static class FilmLikes{
        public FilmLikes(int filmId) {
            this.filmId = filmId;
        }

        int filmId;
        Set<Integer> users = new HashSet<>();

        public int getLikesCount(){
            return users.size();
        }

        public boolean like(int userId){
            if(users.contains(userId)){
                log.info("Нельзя лайкать дважды");
                return false;
            }
            users.add(userId);
            return true;
        }

        public boolean dislike(int userId){
            if(!users.contains(userId)){
                log.info("Убрать лайк можно только к понравившихся фильмов");
                return false;
            }
            users.remove(userId);
            return true;
        }
    }
}
