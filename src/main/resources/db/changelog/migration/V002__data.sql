insert into db_migration.users
values ('Admin', '$2a$10$21DkvNr6bCCSsrRDJi.AheiiyjFgnLbN2nZCXdsIJMbvdAYf41y2W', 'ADMIN'), -- login: Admin, password: 12345678, role: ADMIN
       ('User', '$2a$10$yHpL/vHnVKnk/lk9LkU1FOK1tN9uAUI93nuW32dZJ70GzxlQwKVqO', 'USER');   -- login: User, password: 12345678, role: USER


insert into db_migration.storage (file, user_id)
values ('File1', 1),
       ('File2', 2);
