
set sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

use ordering;

create table if not exists orders
(
    id               bigint auto_increment primary key,
    order_id         varchar(36)    not null,
    restaurant_id    bigint         not null,
    state            varchar(50)    not null,
    order_line_items mediumtext     not null,
    total_amount     decimal(30, 6) not null,
    created_date     datetime(6)    not null,
    unique key uk_orders_order_id (order_id)
);

create table if not exists EVENT_PUBLICATION
(
    ID               varchar(36)  not null,
    LISTENER_ID      varchar(512) not null,
    EVENT_TYPE       varchar(512) not null,
    SERIALIZED_EVENT varchar(4000) not null,
    PUBLICATION_DATE timestamp(6) not null,
    COMPLETION_DATE  timestamp(6) null,
    primary key (ID)
);
