create schema if not exists db_migration;

create table db_migration.users
(
    login    varchar(255),
    password varchar(255),
    role     varchar(10),
    id       serial PRIMARY KEY
);

create table db_migration.storage
(
    file     varchar(255),
    data     bytea,
    user_id  int REFERENCES db_migration.users (id),
    id       serial PRIMARY KEY
);
