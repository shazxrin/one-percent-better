-- liquibase formatted sql

-- changeset rin:1745847150442-1
CREATE TABLE check_ins
(
    id     VARCHAR(255) NOT NULL,
    date   date         NOT NULL,
    count  INTEGER      NOT NULL,
    streak INTEGER      NOT NULL,
    CONSTRAINT pk_check_ins PRIMARY KEY (id)
);

-- changeset rin:1745847150442-2
CREATE TABLE projects
(
    id    VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

