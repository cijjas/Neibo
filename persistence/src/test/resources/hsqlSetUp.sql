SET DATABASE SQL SYNTAX PGS TRUE;

create sequence if not exists amenities_shifts_availability_amenityavailabilityid_seq;
create sequence if not exists amenities_amenityid_seq;
create sequence if not exists channels_channelid_seq;
create sequence if not exists comments_commentid_seq;
create sequence if not exists contacts_contactid_seq;
create sequence if not exists days_dayid_seq;
create sequence if not exists departments_departmentid_seq;
create sequence if not exists events_eventid_seq;
create sequence if not exists images_imageid_seq;
create sequence if not exists neighborhoods_neighborhoodid_seq;
create sequence if not exists posts_postid_seq;
create sequence if not exists products_productid_seq;
create sequence if not exists products_users_inquiries_inquiryid_seq;
create sequence if not exists products_users_purchases_purchaseid_seq;
create sequence if not exists products_users_requests_requestid_seq;
create sequence if not exists professions_professionid_seq;
create sequence if not exists reviews_reviewid_seq;
create sequence if not exists resources_resourceid_seq;
create sequence if not exists shifts_shiftid_seq;
create sequence if not exists tags_tagid_seq;
create sequence if not exists times_timeid_seq;
create sequence if not exists users_availability_bookingid_seq;
create sequence if not exists users_userid_seq;

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
    identification   INTEGER,
    phonenumber     VARCHAR(64)
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

CREATE TABLE IF NOT EXISTS comments
(
    commentid   INTEGER IDENTITY PRIMARY KEY,
    comment     VARCHAR(512) NOT NULL,
    commentdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    workerId            INTEGER IDENTITY PRIMARY KEY,
    phoneNumber         VARCHAR(64)  NOT NULL,
    businessName        VARCHAR(128),
    address             VARCHAR(128) NOT NULL,
    backgroundPictureId INT,
    bio                 VARCHAR(255),
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

-- Create the "users_availability" table (Booking)
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
    remainingUnits INTEGER,
    departmentId INTEGER,
    creationDate DATE,

    FOREIGN KEY (departmentId) references departments(departmentId) ON DELETE CASCADE,
    FOREIGN KEY (primaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (secondaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (tertiaryPictureId) REFERENCES images(imageId) ON DELETE CASCADE,
    FOREIGN KEY (sellerId) REFERENCES users(userId) ON DELETE CASCADE
    );

-- Create Junction Table products_users_inquiries (Inquiries)
CREATE TABLE IF NOT EXISTS products_users_inquiries (
                                                        inquiryId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                                        productId INTEGER,
                                                        userId INTEGER,
                                                        message VARCHAR(512) NOT NULL,
    inquiryDate DATE,
    reply VARCHAR(512),
        FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
    );


-- Create the table with appropriate columns and constraints
CREATE TABLE products_users_requests (
     requestId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
     productid BIGINT,
     userid BIGINT,
     message VARCHAR(512) NOT NULL,
     requestdate TIMESTAMP,
     purchasedate TIMESTAMP,
     units INTEGER,
     status VARCHAR(30),
    FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS neighborhoods_tags (
      neighborhoodid INTEGER NOT NULL,
      tagid INTEGER NOT NULL,
      PRIMARY KEY (neighborhoodid, tagid),
      FOREIGN KEY (neighborhoodid) REFERENCES neighborhoods (neighborhoodid) ON DELETE CASCADE,
      FOREIGN KEY (tagid) REFERENCES tags (tagid) ON DELETE CASCADE
);


