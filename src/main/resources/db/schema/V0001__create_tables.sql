create
    sequence bank_account_id_seq start
    with 1 increment by 1;

create table bank_account
(
    id    integer primary key default nextval('bank_account_id_seq'),
    owner varchar(255),
    name  varchar(255)
);

create
    sequence rider_id_seq start
    with 1 increment by 1;

create table rider
(
    id   integer primary key default nextval('rider_id_seq'),
    name varchar(255)
);

create
    sequence product_id_seq start
    with 1 increment by 1;

create table product
(
    id    integer primary key default nextval('product_id_seq'),
    name  varchar(255),
    price double precision
);

create
    sequence payment_id_seq start
    with 1 increment by 1;

create table payment
(
    id     bigint primary key default nextval('payment_id_seq'),
    date   timestamp with time zone,
    amount double precision
);

create table transference_payment
(
    id              bigint primary key,
    bank_account_id integer,
    foreign key (id) references payment (id) on delete cascade,
    foreign key (bank_account_id) references bank_account (id) on delete cascade
);

create
    sequence order_id_seq start
    with 1 increment by 1;

create table orders
(
    id             bigint primary key default nextval('order_id_seq'),
    date_created   timestamp with time zone,
    customer_name  varchar(255),
    customer_phone varchar(255),
    note           varchar(255),
    discount       double precision
);

create
    sequence order_item_id_seq start
    with 1 increment by 1;

create table order_item
(
    id          bigint primary key default nextval('order_item_id_seq'),
    order_id    bigint not null,
    position    integer,
    product_id  integer,
    quantity    integer,
    unit_price  double precision,
    total_price double precision,
    note        varchar(255),
    foreign key (order_id) references orders (id) on delete cascade,
    foreign key (product_id) references product (id) on delete cascade
);

create table order_payment
(
    order_id   bigint not null,
    payment_id bigint not null,
    foreign key (order_id) references orders (id) on delete cascade,
    foreign key (payment_id) references payment (id) on delete cascade
);


create table orders_delivery
(
    order_id   bigint not null,
    address    varchar(255),
    price      double precision,
    rider_id   integer,
    delivered  boolean,
    date       timestamp with time zone,
    date_range timestamp with time zone,
    foreign key (order_id) references orders (id) on delete cascade,
    foreign key (rider_id) references rider (id) on delete cascade
);
