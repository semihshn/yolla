
set sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- create initial data

use payment;

create table if not exists payments
(
    id           bigint auto_increment primary key,
    restaurant_id bigint       not null,
    order_id     varchar(36)  not null,
    payment_id   varchar(36)  not null,
    total_amount decimal(30, 6) not null,
    state        varchar(50)  not null,
    created_date datetime     not null,
    modified_date datetime    null,
    constraint uk_payments_order_id unique (order_id),
    constraint uk_payments_payment_id unique (payment_id)
);
