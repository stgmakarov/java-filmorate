CREATE TABLE "Film" (
  "id" int PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "releaseDate" Date,
  "duration" int,
  "genre_id" int,
  "rating_id" int
);

CREATE TABLE "FilmLikes" (
  "film_id" int,
  "user_id" int,
  PRIMARY KEY ("film_id", "user_id")
);

CREATE TABLE "User" (
  "id" int PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" Date
);

CREATE TABLE "Friends" (
  "user_id" int,
  "friend_id" int,
  "confirmed" boolean,
  PRIMARY KEY ("user_id", "friend_id")
);

CREATE TABLE "Genre" (
  "id" int PRIMARY KEY,
  "description" varchar
);

CREATE TABLE "Rating" (
  "id" int PRIMARY KEY,
  "name" varchar
);

ALTER TABLE "Film" ADD FOREIGN KEY ("genre_id") REFERENCES "Genre" ("id");

ALTER TABLE "Film" ADD FOREIGN KEY ("rating_id") REFERENCES "Rating" ("id");

ALTER TABLE "FilmLikes" ADD FOREIGN KEY ("film_id") REFERENCES "Film" ("id");

ALTER TABLE "FilmLikes" ADD FOREIGN KEY ("user_id") REFERENCES "User" ("id");

ALTER TABLE "Friends" ADD FOREIGN KEY ("user_id") REFERENCES "Film" ("id");

ALTER TABLE "Friends" ADD FOREIGN KEY ("friend_id") REFERENCES "Film" ("id");
