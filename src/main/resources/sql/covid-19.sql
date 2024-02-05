create database covid19;
use covid19;

create table direction(
    id int primary key AUTO_INCREMENT,
    direction_name varchar(20)
);

create table country(
    id int primary key AUTO_INCREMENT,
    country_name varchar(100)
);

create table affects(
    id int primary key AUTO_INCREMENT,
    direction_id int,
    year int,
    date Date,
    weekday varchar(10),
    country_id int,
    commodity varchar(100),
    transport_mode varchar(10),
    measure varchar(10),
    value bigint,
    cumulative bigint,
    foreign key (direction_id) references direction(id),
    foreign key (country_id) references country(id)
);