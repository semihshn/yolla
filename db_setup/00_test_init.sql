drop database if exists inventory;
drop database if exists ordering;
drop database if exists payment;
drop database if exists shipping;

create database inventory;
create database ordering;
create database payment;
create database shipping;

create schema if not exists inventory character set = utf8mb4 collate = utf8mb4_unicode_ci;
create schema if not exists ordering character set = utf8mb4 collate = utf8mb4_unicode_ci;
create schema if not exists payment character set = utf8mb4 collate = utf8mb4_unicode_ci;
create schema if not exists shipping character set = utf8mb4 collate = utf8mb4_unicode_ci;