--liquibase formatted sql

--changeset nfisher:1

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists Splits (
                                     split_id uuid DEFAULT uuid_generate_v4 (),
                                     owner varchar,
                                     members text[],
                                     amount varchar,
                                     created_at timestamp,
                                     updated_at timestamp ,
                                     PRIMARY KEY (split_id)
)

