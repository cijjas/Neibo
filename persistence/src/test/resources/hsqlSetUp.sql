SET DATABASE SQL SYNTAX PGS TRUE;

-- S1
-- core
drop table if exists neighborhoods CASCADE;
drop table if exists channels CASCADE;
drop table if exists tags CASCADE;
drop table if exists posts CASCADE;
drop table if exists comments CASCADE;
drop table if exists posts_tags CASCADE;
drop table if exists users CASCADE; -- used to be neighbors
drop table if exists posts_users_subscriptions CASCADE;
-- used to be posts_neighbors

-- S2
-- amenities
drop table if exists amenities CASCADE;
drop table if exists reservations CASCADE;
drop table if exists hours CASCADE;
-- events
drop table if exists events CASCADE;
-- miscellaneous
drop table if exists images CASCADE;
drop table if exists contacts cascade;
drop table if exists resources cascade;
-- channel uniqueness
drop table if exists neighborhoods_channels cascade;

-- S3
-- events attendance
drop table if exists events_users cascade;
-- refactor amenities
drop table if exists amenities_hours cascade;
-- post likes
drop table if exists posts_users_likes cascade;
-- amenity refactor
drop table if exists times cascade;
drop table if exists days cascade;
drop table if exists shifts cascade;
drop table if exists users_availability cascade;
drop table if exists amenities_shifts_availability cascade;

-- workers
drop table if exists workers_info cascade;
drop table if exists workers_professions cascade;
drop table if exists professions cascade;
drop table if exists workers_neighborhoods cascade;
drop table if exists reviews cascade;

drop table products cascade;
drop table products_users_inquiries cascade;
drop table products_users_requests cascade;
drop table departments cascade;


CREATE TABLE IF NOT EXISTS neighborhoods
(
    neighborhoodid   INTEGER IDENTITY PRIMARY KEY,
    neighborhoodname VARCHAR(128)
);


CREATE TABLE IF NOT EXISTS channels
(
    channelid INTEGER IDENTITY PRIMARY KEY,
    channel   VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS images
(
    imageid INTEGER IDENTITY PRIMARY KEY,
    image   LONGVARBINARY NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    userid           INTEGER IDENTITY PRIMARY KEY,
    mail             VARCHAR(128) NOT NULL UNIQUE,
    name             VARCHAR(64)  NOT NULL,
    surname          VARCHAR(64)  NOT NULL,
    creationdate     TIMESTAMP    NOT NULL,
    neighborhoodid   INTEGER      NOT NULL REFERENCES neighborhoods ON DELETE CASCADE,
    password         VARCHAR(128),
    darkmode         BOOLEAN,
    language         VARCHAR(32),
    role             VARCHAR(64),
    profilepictureid INTEGER      REFERENCES images ON DELETE SET NULL,
    identification   INTEGER
);

CREATE TABLE IF NOT EXISTS amenities
(
    amenityid      INTEGER IDENTITY PRIMARY KEY,
    name           VARCHAR(512) NOT NULL,
    description    LONGVARCHAR  NOT NULL,
    neighborhoodid INTEGER      NOT NULL REFERENCES neighborhoods ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS neighborhoods_channels
(
    neighborhoodid INTEGER NOT NULL REFERENCES neighborhoods ON DELETE CASCADE,
    channelid      INTEGER NOT NULL REFERENCES channels ON DELETE CASCADE,
    PRIMARY KEY (neighborhoodid, channelid)
);

CREATE TABLE IF NOT EXISTS tags
(
    tagid INTEGER IDENTITY PRIMARY KEY,
    tag   VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS posts
(
    postid        INTEGER IDENTITY PRIMARY KEY,
    title         VARCHAR(128) NOT NULL,
    description   LONGVARCHAR  NOT NULL,
    postdate      TIMESTAMP    NOT NULL,
    userid        INTEGER      NOT NULL REFERENCES users ON DELETE CASCADE,
    channelid     INTEGER      NOT NULL REFERENCES channels ON DELETE CASCADE,
    postpictureid INTEGER      REFERENCES images ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS posts_tags
(
    postid INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    tagid  INTEGER NOT NULL REFERENCES tags ON DELETE CASCADE,
    PRIMARY KEY (postid, tagid)
);


CREATE TABLE IF NOT EXISTS posts_users_likes
(
    postid   INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    userid   INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    likedate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (postid, userid)
);

CREATE TABLE IF NOT EXISTS posts_users_subscriptions
(
    userid INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    postid INTEGER NOT NULL REFERENCES posts ON DELETE CASCADE,
    PRIMARY KEY (postid, userid)
);

CREATE TABLE IF NOT EXISTS comments
(
    commentid   INTEGER IDENTITY PRIMARY KEY,
    comment     VARCHAR(512) NOT NULL,
    commentdate DATE         NOT NULL,
    userid      INTEGER      NOT NULL REFERENCES users ON DELETE CASCADE,
    postid      INTEGER      NOT NULL REFERENCES posts ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resources
(
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
    neighborhoodid integer     not null references neighborhoods
);


-- Create a table to store additional information for workers
CREATE TABLE IF NOT EXISTS workers_info
(
    workerId            INT,
    phoneNumber         VARCHAR(64)  NOT NULL,
    businessName        VARCHAR(128),
    address             VARCHAR(128) NOT NULL,
    backgroundPictureId INT,
    bio                 VARCHAR(255),
    PRIMARY KEY (workerId),
    FOREIGN KEY (workerId) REFERENCES users (userId) ON DELETE CASCADE
);

-- Create Professions
CREATE TABLE IF NOT EXISTS professions
(
    professionId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    profession   VARCHAR(64) NOT NULL UNIQUE
);

-- Create Junction Table workers_neighborhoods
CREATE TABLE IF NOT EXISTS workers_neighborhoods
(
    workerId       INT,
    neighborhoodId INT,
    role VARCHAR(128),
    PRIMARY KEY (workerId, neighborhoodId),
    FOREIGN KEY (workerId) REFERENCES users (userId) ON DELETE CASCADE,
    FOREIGN KEY (neighborhoodId) REFERENCES neighborhoods (neighborhoodId) ON DELETE CASCADE
);

-- Create Junction Table workers_tags (renamed to workers_professions)
CREATE TABLE IF NOT EXISTS workers_professions
(
    workerId     INT,
    professionId INT,
    PRIMARY KEY (workerId, professionId),
    FOREIGN KEY (workerId) REFERENCES users (userId) ON DELETE CASCADE,
    FOREIGN KEY (professionId) REFERENCES professions (professionId) ON DELETE CASCADE
);

-- Create Junction Table reviews (users_workers)
CREATE TABLE IF NOT EXISTS reviews
(
    reviewId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    workerId INT,
    userId   INT,
    rating   FLOAT,
    review   VARCHAR(255),
    date     TIMESTAMP NOT NULL,
    FOREIGN KEY (workerId) REFERENCES users (userId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (userId) ON DELETE CASCADE
);


-- Create the "times" table
CREATE TABLE times
(
    timeId       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timeInterval TIME
);

CREATE TABLE IF NOT EXISTS events
(
    eventid        INTEGER IDENTITY PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    LONGVARCHAR,
    date           DATE         NOT NULL,
    starttimeid    INTEGER      NOT NULL REFERENCES times ON DELETE CASCADE,
    endtimeid      INTEGER      NOT NULL REFERENCES times ON DELETE CASCADE,
    neighborhoodid INTEGER      NOT NULL REFERENCES neighborhoods ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events_users
(
    userid  INTEGER NOT NULL REFERENCES users ON DELETE CASCADE,
    eventid INTEGER NOT NULL REFERENCES events ON DELETE CASCADE,
    PRIMARY KEY (eventid, userid)
);

-- Create the "days" table
CREATE TABLE days
(
    dayId   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    dayName VARCHAR(20) UNIQUE
);

-- Create the "shifts" table
CREATE TABLE shifts
(
    shiftId   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    dayId     INTEGER REFERENCES days (dayId),
    startTime INTEGER REFERENCES times (timeId)
);

-- Create the "amenities_shifts_availability" table
CREATE TABLE amenities_shifts_availability
(
    amenityAvailabilityId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    amenityId             INTEGER REFERENCES amenities (amenityid), -- Assuming amenities table exists
    shiftId               INTEGER REFERENCES shifts (shiftId),
    UNIQUE (amenityId, shiftId)
);

-- Create the "users_availability" table
CREATE TABLE users_availability
(
    bookingId             INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    amenityAvailabilityId INTEGER REFERENCES amenities_shifts_availability (amenityAvailabilityId) ON DELETE CASCADE,
    userId                INTEGER REFERENCES users (userid), -- Assuming users table exists
    date                  DATE,
    UNIQUE (amenityAvailabilityId, date)
);

-- Create Table departments
CREATE TABLE IF NOT EXISTS departments (
                                           departmentId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                           department VARCHAR(64) NOT NULL UNIQUE
);

-- Create a new table for products:
CREATE TABLE IF NOT EXISTS products (
                                        productId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                        name VARCHAR(128) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    price DOUBLE NOT NULL,
    used BOOLEAN NOT NULL,
    primaryPictureId INTEGER,
    secondaryPictureId INTEGER,
    tertiaryPictureId INTEGER,
    sellerId INTEGER NOT NULL,
    buyerId INTEGER,
    departmentId INTEGER,
    creationDate DATE,
    purchaseDate DATE,

    FOREIGN KEY (departmentId) references departments(departmentId) ON DELETE CASCADE,
    FOREIGN KEY (primaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (secondaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (tertiaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (sellerId) REFERENCES users(userId) ON DELETE CASCADE,
    FOREIGN KEY (buyerId) REFERENCES users(userId) ON DELETE CASCADE

    );

-- Create Junction Table products_users_inquiries (Inquiries)
CREATE TABLE IF NOT EXISTS products_users_inquiries (
                                                        inquiryId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                                        productId INTEGER,
                                                        userId INTEGER,
                                                        message VARCHAR(512) NOT NULL,
    reply VARCHAR(512),
    FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
    );

-- Create Junction Table products_users_requests (Requests)
CREATE TABLE IF NOT EXISTS products_users_requests (
    requestId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    productId INTEGER,
    userId INTEGER,
    message VARCHAR(512) NOT NULL,

    FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
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

