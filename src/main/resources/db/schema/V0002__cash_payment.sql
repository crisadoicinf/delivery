create sequence cash_payment_id_seq start with 1 increment by 1;
create table cash_payment (
    id bigint primary key,
    rider_id integer,
    foreign key (id) references payment (id) on delete cascade,
    foreign key (rider_id) references rider (id) on delete cascade
);