CREATE TABLE payments (
    id bigserial PRIMARY KEY,
    amount numeric(19,2) NOT NULL,
    name varchar(100),
    number varchar(19),
    expiration varchar(7),
    code varchar(3),
    status varchar(255) NOT NULL,
    payment_method_id bigint NOT NULL,
    order_id bigint NOT NULL
);