set sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- create initial data

-- payment tables
use payment;

create table if not exists payments
(
    id                 bigint auto_increment primary key,
    created_date       datetime          not null,
    modified_date      datetime          null,
    status             tinyint default 0 not null comment '-1: deleted, 0:passive, 1:active',
    restaurant_id      bigint            not null,
    order_id           varchar(255)      not null,
    payment_id         varchar(255)      not null,
    state              varchar(50)       not null,
    ordered_menu_items MEDIUMTEXT        not null
);

create table if not exists processed_events
(
    id            bigint auto_increment primary key,
    created_date  datetime          not null,
    modified_date datetime          null,
    status        tinyint default 0 not null comment '-1: deleted, 0:passive, 1:active',
    aggregate_id  varchar(50)       not null,
    UNIQUE (aggregate_id)
);