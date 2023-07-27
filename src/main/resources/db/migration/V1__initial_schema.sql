create table client
(
    id           bigserial primary key,
    email        varchar(255) unique,
    first_name   varchar(255),
    last_name    varchar(255),
    middle_name  varchar(255),
    phone_number varchar(255) not null unique,
    status       varchar(255),
    created      timestamp,
    updated      timestamp
);


create table dish_type
(
    id           bigserial primary key,
    is_available boolean default true not null,
    title        varchar(255)         not null
);

create table dish
(
    id             bigserial primary key,
    currency_price numeric(19, 2)       not null,
    is_available   boolean default true not null,
    title          varchar(255)         not null,
    dish_type_id   bigint references dish_type
);


create table image_data
(
    id                 bigserial primary key,
    bytes              bytea,
    mime_type          varchar(255),
    original_file_name varchar(255) not null unique
);

create table dish_details
(
    id               bigserial primary key,
    composition_text varchar(255) not null,
    measure_text     varchar(255) not null,
    dish_id          bigint       not null references dish,
    image_data_id    bigint references image_data
);


create table pick_up_location
(
    id                     bigserial primary key,
    additional_information varchar(255),
    city_name              varchar(255),
    house_data             varchar(255),
    latitude               double precision,
    longitude              double precision,
    priority               integer,
    street_name            varchar(255),
    title                  varchar(255)
);



create table "order"
(
    id                  bigserial primary key,
    created             timestamp,
    payment_method      varchar(255),
    payment_status      varchar(255),
    receiving_code      varchar(255),
    status              varchar(255),
    updated             timestamp,
    client_id           bigint not null references client,
    pick_up_location_id bigint references pick_up_location
);


create table order_item
(
    id       bigserial primary key,
    count    integer not null,
    dish_id  bigint  not null references dish,
    order_id bigint  not null references "order"
);



