--liquibase formatted sql

--changeset nfisher:1


CREATE TABLE if not exists Users (
    user_id varchar,
    first_name varchar,
    last_name varchar,
    email varchar ,
    owned_splits text[],
    member_of_splits text[],
    friends text[],
    amount_owe jsonb,
    amount_get jsonb,
    created_at timestamp,
    PRIMARY KEY (user_id)
)

