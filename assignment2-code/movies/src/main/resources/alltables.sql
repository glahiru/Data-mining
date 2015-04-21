create database lahiru_ginnaliya;
use lahiru_ginnaliya;
create table movies(
  movieId decimal(8,2),
  name varchar(100),
  year varchar(20),
  genre varchar(50),
  primary key(movieId, genre)
);
create table ratings(
  userId decimal(8,2),
  movieId decimal(8,2),
  rating  decimal (8,2),
  time timestamp,
  primary key(userId,movieId)
);

create table users(
userId decimal (8,2),
gender varchar(1),
age decimal(8,2),
occupation varchar(20),
zipCode varchar(10),
primary key(userId)
);

create table usersRating(
userId decimal(8,2),
movieId decimal(8,2),
rating  decimal (8,2),
gender varchar(1),
age decimal(8,2),
occupation varchar(20),
zipCode varchar(10),
time timestamp,
primary key(userId,movieId)
);

create table userMovieRating(
userId decimal(8,2),
movieId decimal(8,2),
rating  decimal (8,2),
gender varchar(1),
age decimal(8,2),
occupation varchar(20),
zipCode varchar(10),
time timestamp,
genre varchar(20)
);

/*
/* checking for malformed data*/
select * from movies where movieId is null or name is null or year is null or genre is null;
select * from users where age is null or userId is null or gender is null or occupation is null or zipCode is null;

/* 2.(iii)checking the mail and femail records*/
select users.gender,count(users.userId) from users left join ratings on users.userId=ratings.userId group by users.gender;


/* 2.(ii)doing search for computer scientist*/
insert into usersRating(userId,movieId,rating,gender,age,occupation,zipCode,time)
select users.userId,ratings.movieId,ratings.rating,users.gender,users.age,users.occupation,users.zipCode,ratings.time
from users join ratings on users.userId=ratings.userId and users.occupation=12;
select userId,movieId,count(distinct gender) as genderCount,rating from usersRating group by movieId having count(distinct(gender))>1 and count(distinct(rating))=1;
select userId,movieId,count(distinct gender) as genderCount,rating from usersRating group by movieId having count(distinct(gender))>1;

/* answer for 2.(iv)*/

select count(rating),genre from users join ratings on users.userId=ratings.userId join movies on ratings.movieId=movies.movieId group by movies.genre;*/