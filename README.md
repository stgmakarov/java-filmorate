# java-filmorate
Каталог фильмов и не только!
![ER диаграма](https://github.com/stgmakarov/java-filmorate/blob/main/ERDiagrams/Filmorate.png)

SQL выражения:
1. Выборка лучших фильмов 
SELECT * FROM "Film"
   WHERE id IN
   (SELECT film_id
   FROM "FilmLikes"
   GROUP BY film_id
   ORDER BY COUNT(film_id) DESC
   LIMIT 10)
2. Общие друзья
select * from "User" where id in
   (select friend_id from "Friends"
   where user_id = 1 and
   confirmed = true and
   friend_id in ( select friend_id from "Friends"
   where user_id = 5 and
   confirmed = true ) )
