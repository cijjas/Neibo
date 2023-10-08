SET DATABASE SQL SYNTAX PGS TRUE;

DROP TABLE IF EXISTS neighborhoods CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS hours CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS amenities CASCADE;
DROP TABLE IF EXISTS amenities_hours CASCADE;
DROP TABLE IF EXISTS neighborhoods_channels CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS posts_tags CASCADE;
DROP TABLE IF EXISTS posts_users_likes CASCADE;
DROP TABLE IF EXISTS posts_users_subscriptions CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS events_users CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS resources CASCADE;


CREATE TABLE IF NOT EXISTS neighborhoods (
    neighborhoodid   INTEGER IDENTITY PRIMARY KEY,
    neighborhoodname VARCHAR(128)
);


CREATE TABLE IF NOT EXISTS channels (
    channelid INTEGER IDENTITY PRIMARY KEY,
    channel   VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS images (
    imageid INTEGER IDENTITY PRIMARY KEY,
    image   LONGVARBINARY NOT NULL
);

CREATE TABLE IF NOT EXISTS hours (
        hoursid   INTEGER IDENTITY PRIMARY KEY,
        weekday   VARCHAR(128) NOT NULL,
        opentime  TIME NOT NULL,
        closetime TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    userid           INTEGER IDENTITY PRIMARY KEY,
    mail             VARCHAR(128) NOT NULL UNIQUE,
    name             VARCHAR(64) NOT NULL,
    surname          VARCHAR(64) NOT NULL,
    creationdate     TIMESTAMP NOT NULL,
    neighborhoodid   INTEGER NOT NULL REFERENCES neighborhoods ON DELETE CASCADE,
    password         VARCHAR(128),
    darkmode         BOOLEAN,
    language         VARCHAR(32),
    role             VARCHAR(64),
    profilepictureid INTEGER REFERENCES images ON DELETE SET NULL,
    identification   INTEGER
);

CREATE TABLE IF NOT EXISTS amenities (
    amenityid      INTEGER IDENTITY PRIMARY KEY,
    name           VARCHAR(512) NOT NULL,
    description    LONGVARCHAR NOT NULL,
    neighborhoodid INTEGER NOT NULL REFERENCES neighborhoods ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS amenities_hours (
    amenityid INTEGER NOT NULL REFERENCES amenities ON DELETE CASCADE,
    hoursid   INTEGER NOT NULL REFERENCES hours ON DELETE CASCADE,
    PRIMARY KEY (amenityid, hoursid)
);

CREATE TABLE IF NOT EXISTS neighborhoods_channels (
    neighborhoodid INTEGER NOT NULL REFERENCES neighborhoods ON DELETE CASCADE,
    channelid      INTEGER NOT NULL REFERENCES channels ON DELETE CASCADE,
    PRIMARY KEY (neighborhoodid, channelid)
);

CREATE TABLE IF NOT EXISTS tags (
    tagid INTEGER IDENTITY PRIMARY KEY,
    tag   VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS posts (
    postid        INTEGER IDENTITY PRIMARY KEY,
    title         VARCHAR(128) NOT NULL,
    description   LONGVARCHAR NOT NULL,
    postdate      TIMESTAMP NOT NULL,
    userid        INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    channelid     INTEGER NOT NULL REFERENCES channels ON DELETE CASCADE,
    postpictureid INTEGER REFERENCES images ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS posts_tags (
    postid INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    tagid  INTEGER NOT NULL REFERENCES tags ON DELETE CASCADE,
    PRIMARY KEY (postid, tagid)
);


CREATE TABLE IF NOT EXISTS posts_users_likes (
    postid   INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    userid   INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    likedate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (postid, userid)
);

CREATE TABLE IF NOT EXISTS posts_users_subscriptions (
    userid INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    postid INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    PRIMARY KEY (postid, userid)
);

CREATE TABLE IF NOT EXISTS comments (
    commentid   INTEGER IDENTITY PRIMARY KEY,
    comment     VARCHAR(512) NOT NULL,
    commentdate DATE NOT NULL,
    userid      INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    postid      INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events (
    eventid        INTEGER IDENTITY PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    LONGVARCHAR,
    date           DATE NOT NULL,
    duration       INTEGER,
    neighborhoodid INTEGER NOT NULL REFERENCES neighborhoods ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events_users (
    userid  INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    eventid INTEGER NOT NULL REFERENCES events ON DELETE CASCADE,
    PRIMARY KEY (eventid, userid)
);

CREATE TABLE IF NOT EXISTS reservations (
    reservationid INTEGER IDENTITY PRIMARY KEY,
    date          DATE NOT NULL,
    starttime     TIME NOT NULL,
    endtime       TIME NOT NULL,
    userid        INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    amenityid     INTEGER NOT NULL REFERENCES amenities ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resources (
    resourceid          INTEGER IDENTITY PRIMARY KEY,
    resourcetitle       VARCHAR(64),
    resourcedescription VARCHAR(255),
    resourceimageid     INTEGER REFERENCES images ON DELETE CASCADE,
    neighborhoodid      INTEGER NOT NULL REFERENCES neighborhoods
);

create table contacts
(
    contactid      INTEGER IDENTITY PRIMARY KEY,
    contactname    varchar(64) not null,
    contactaddress varchar(128),
    contactphone   varchar(32),
    neighborhoodid integer not null references neighborhoods
);

/*

-- Insert into neighborhoods
INSERT INTO neighborhoods (neighborhoodname) VALUES ('Sample Neighborhood 1');
INSERT INTO neighborhoods (neighborhoodname) VALUES ('Sample Neighborhood 2');

-- Insert into channels
INSERT INTO channels (channel) VALUES ('Sample Channel 1');
INSERT INTO channels (channel) VALUES ('Sample Channel 2');

-- Insert into hours
INSERT INTO hours (weekday, opentime, closetime) VALUES ('Monday', '08:00:00', '17:00:00');
INSERT INTO hours (weekday, opentime, closetime) VALUES ('Tuesday', '09:00:00', '18:00:00');

-- Insert into users, password is encryption for 'sample'
INSERT INTO users (mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role)
VALUES ('sampleuser1@example.com', 'John', 'Doe', TIMESTAMP '2023-05-15 12:00:00', 0, '$2a$10$Z/kjpr39xSAXk4VquxeJUO2chfWFVzijUOrrtZ3DeG1YjrnBCzrKS', false, 'ENGLISH', 'NEIGHBOR');
INSERT INTO users (mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role)
VALUES ('sampleuser2@example.com', 'Jane', 'Smith', TIMESTAMP '2023-05-16 13:00:00', 1, '$2a$10$Z/kjpr39xSAXk4VquxeJUO2chfWFVzijUOrrtZ3DeG1YjrnBCzrKS', true, 'SPANISH', 'ADMIN');

-- Insert into amenities
INSERT INTO amenities (name, description, neighborhoodid) VALUES ('Sample Amenity 1', 'This is a sample amenity 1.', 0);
INSERT INTO amenities (name, description, neighborhoodid) VALUES ('Sample Amenity 2', 'This is a sample amenity 2.', 1);

-- Insert into amenities_hours
INSERT INTO amenities_hours (amenityid, hoursid) VALUES (0, 0);
INSERT INTO amenities_hours (amenityid, hoursid) VALUES (1, 1);

-- Insert into neighborhoods_channels
INSERT INTO neighborhoods_channels (neighborhoodid, channelid) VALUES (0, 0);
INSERT INTO neighborhoods_channels (neighborhoodid, channelid) VALUES (1, 1);

-- Insert into tags
INSERT INTO tags (tag) VALUES ('Sample Tag 1');
INSERT INTO tags (tag) VALUES ('Sample Tag 2');

-- Insert into posts
INSERT INTO posts (title, description, postdate, userid, channelid) VALUES ('Sample Post 1', 'This is a sample post 1.', TIMESTAMP '2023-05-15 12:00:00', 0, 0);
INSERT INTO posts (title, description, postdate, userid, channelid) VALUES ('Sample Post 2', 'This is a sample post 2.', TIMESTAMP '2023-05-16 13:00:00', 1, 1);

-- Insert into posts_tags
INSERT INTO posts_tags (postid, tagid) VALUES (0, 0);
INSERT INTO posts_tags (postid, tagid) VALUES (1, 1);

-- Insert into posts_users_likes
INSERT INTO posts_users_likes (postid, userid) VALUES (0, 0);
INSERT INTO posts_users_likes (postid, userid) VALUES (1, 1);

-- Insert into posts_users_subscriptions
INSERT INTO posts_users_subscriptions (userid, postid) VALUES (0, 0);
INSERT INTO posts_users_subscriptions (userid, postid) VALUES (1, 1);

-- Insert into comments
INSERT INTO comments (comment, commentdate, userid, postid) VALUES ('This is a sample comment 1.', CURRENT_TIMESTAMP, 0, 0);
INSERT INTO comments (comment, commentdate, userid, postid) VALUES ('This is a sample comment 2.', CURRENT_TIMESTAMP, 1, 1);

-- Insert into events
INSERT INTO events (name, description, date, duration, neighborhoodid) VALUES ('Sample Event 1', 'This is a sample event 1.', DATE '2023-08-15', 0, 0);
INSERT INTO events (name, description, date, duration, neighborhoodid) VALUES ('Sample Event 2', 'This is a sample event 2.', DATE '2023-08-16', 1, 1);

-- Insert into events_users
INSERT INTO events_users (userid, eventid) VALUES (0, 0);
INSERT INTO events_users (userid, eventid) VALUES (1, 1);

-- Insert into reservations
INSERT INTO reservations (date, starttime, endtime, userid, amenityid) VALUES (DATE '2023-08-15', TIME '10:00:00', TIME '12:00:00', 0, 0);
INSERT INTO reservations (date, starttime, endtime, userid, amenityid) VALUES (DATE '2023-08-16', TIME '11:00:00', TIME '13:00:00', 1, 1);

-- Insert into resources
INSERT INTO resources (resourcetitle, resourcedescription, resourceimageid, neighborhoodid) VALUES ('Sample Resource 1', 'This is a sample resource 1.', NULL, 0);
INSERT INTO resources (resourcetitle, resourcedescription, resourceimageid, neighborhoodid) VALUES ('Sample Resource 2', 'This is a sample resource 2.', NULL, 1);




*/

