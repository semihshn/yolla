
set sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- create initial data

-- ordering tables
use ordering;

create table if not exists orders
(
    id                bigint auto_increment primary key,
    created_date      datetime      not null,
    modified_date     datetime      null,
    status            tinyint default 0 not null comment '-1: deleted, 0:passive, 1:active',
    restaurant_id     bigint        not null,
    order_id          varchar(255)  not null,
    state             varchar(50)   not null,
    order_line_items  MEDIUMTEXT    not null,
    total_amount       decimal(30,6) not null
);