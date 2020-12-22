create table users
(
    user_id    bigserial
        constraint pk_users primary key,
    username   text,
    email      text,
    full_name  text,
    birthday   date,
    sex        smallint,
    created_at timestamptz,
    updated_at timestamptz
);

create unique index idx_users_username on users (username);
create unique index idx_users_email on users (email);

comment on table users is 'Users of the system';
comment on column users.sex is '0 - male, 1 - female';

