------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------- GENERATION -------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------

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
create sequence if not exists products_users_requests_requestid_seq;
create sequence if not exists professions_professionid_seq;
create sequence if not exists reviews_reviewid_seq;
create sequence if not exists resources_resourceid_seq;
create sequence if not exists shifts_shiftid_seq;
create sequence if not exists tags_tagid_seq;
create sequence if not exists times_timeid_seq;
create sequence if not exists users_availability_bookingid_seq;
create sequence if not exists users_userid_seq;

create table if not exists neighborhoods
(
    neighborhoodid bigint default nextval('neighborhoods_neighborhoodid_seq'::regclass) not null
        constraint neighborhoods_pkey
            primary key,
    neighborhoodname varchar(128),
    isbase boolean default false not null
);

create table if not exists tags
(
    tagid bigint default nextval('tags_tagid_seq'::regclass) not null
        constraint tags_pkey
            primary key,
    tag varchar(64) not null
        constraint tags_tag_key
            unique
);

create table if not exists channels
(
    channelid bigint default nextval('tags_tagid_seq'::regclass) not null
        constraint channels_pkey
            primary key,
    channel varchar(64) not null
        constraint channels_channel_key
            unique,
    isbase boolean default false not null
);

create table if not exists images
(
    imageid bigint default nextval('images_imageid_seq'::regclass) not null
        constraint images_pkey
            primary key,
    image bytea not null
);

create table if not exists users
(
    userid bigint default nextval('users_userid_seq'::regclass) not null
        constraint users_pkey
            primary key,
    mail varchar(128) not null
        constraint users_mail_key
            unique,
    name varchar(64) not null,
    surname varchar(64) not null,
    creationdate timestamp not null,
    neighborhoodid bigint not null
        constraint users_neighborhoodid_fkey
            references neighborhoods
            on delete cascade,
    password varchar(128),
    darkmode boolean,
    language varchar(32),
    role varchar(64),
    profilepictureid bigint
        constraint fk_users_images
            references images
            on delete set null,
    identification integer,
    phonenumber varchar(255)
);

create table if not exists posts
(
    postid bigint default nextval('posts_postid_seq'::regclass) not null
        constraint posts_pkey
            primary key,
    title varchar(128) not null,
    description text not null,
    postdate timestamp not null,
    userid bigint not null
        constraint fk_posts_users
            references users
            on delete cascade,
    channelid bigint not null
        constraint posts_channelid_fkey
            references channels
            on delete cascade,
    postpictureid bigint
        constraint fk_posts_images
            references images
            on delete set null
);

create table if not exists comments
(
    commentid bigint default nextval('comments_commentid_seq'::regclass) not null
        constraint comments_pkey
            primary key,
    comment varchar(512) not null,
    commentdate timestamp not null,
    userid bigint not null
        constraint fk_comments_users
            references users
            on delete cascade,
    postid bigint not null
        constraint comments_postid_fkey
            references posts
            on delete cascade
);

create table if not exists posts_tags
(
    postid bigint not null
        constraint posts_tags_postid_fkey
            references posts
            on delete cascade,
    tagid bigint not null
        constraint posts_tags_tagid_fkey
            references tags
            on delete cascade,
    constraint posts_tags_pkey
        primary key (postid, tagid)
);

create table if not exists neighborhoods_channels
(
    neighborhoodid bigint not null
        constraint neighborhoods_channels_neighborhoodid_fkey
            references neighborhoods
            on delete cascade,
    channelid bigint not null
        constraint neighborhoods_channels_channelid_fkey
            references channels
            on delete cascade,
    constraint neighborhoods_channels_pkey
        primary key (neighborhoodid, channelid)
);

create table if not exists resources
(
    resourceid bigint default nextval('resources_resourceid_seq'::regclass) not null
        constraint resources_pkey
            primary key,
    resourcetitle varchar(64),
    resourcedescription varchar(255),
    resourceimageid bigint
        constraint resources_resourceimageid_fkey
            references images on delete set null,
    neighborhoodid bigint not null
        constraint resources_neighborhoodid_fkey
            references neighborhoods
);

create table if not exists contacts
(
    contactid bigint default nextval('contacts_contactid_seq'::regclass) not null
        constraint contacts_pkey
            primary key,
    contactname varchar(64) not null,
    contactaddress varchar(128),
    contactphone varchar(32),
    neighborhoodid bigint not null
        constraint contacts_neighborhoodid_fkey
            references neighborhoods
);

create table if not exists amenities
(
    amenityid bigint default nextval('amenities_amenityid_seq'::regclass) not null
        constraint amenities_pkey
            primary key,
    name varchar(512) not null,
    description varchar(512) not null,
    neighborhoodid bigint not null
        constraint amenities_neighborhoodid_fkey
            references neighborhoods
            on delete cascade
);

create table if not exists posts_users_likes
(
    postid bigint not null
        constraint posts_users_likes_postid_fkey
            references posts
            on delete cascade,
    userid bigint not null
        constraint posts_users_likes_userid_fkey
            references users
            on delete cascade,
    likedate timestamp default CURRENT_TIMESTAMP,
    constraint posts_users_likes_pkey
        primary key (postid, userid)
);

create table if not exists workers_info
(
    workerid bigint not null
        constraint workers_info_pkey
            primary key
        constraint workers_info_workerid_fkey
            references users
            on delete cascade,
    phonenumber varchar(64) not null,
    businessname varchar(128),
    address varchar(128) not null,

    backgroundpictureid bigint
        constraint fk_users_images
            references images
            on delete set null,
    bio varchar(255)
);

create table if not exists professions
(
    professionid bigint default nextval('professions_professionid_seq'::regclass) not null
        constraint professions_pkey
            primary key,
    profession varchar(64) not null
        constraint professions_profession_key
            unique
);

create table if not exists workers_neighborhoods
(
    workerid bigint not null
        constraint workers_neighborhoods_workerid_fkey
            references users
            on delete cascade,
    neighborhoodid bigint not null
        constraint workers_neighborhoods_neighborhoodid_fkey
            references neighborhoods
            on delete cascade,
    role varchar(255),
    requestDate timestamp not null,
    constraint workers_neighborhoods_pkey
        primary key (workerid, neighborhoodid)
);

create table if not exists workers_professions
(
    workerid bigint not null
        constraint workers_professions_workerid_fkey
            references users
            on delete cascade,
    professionid bigint not null
        constraint workers_professions_professionid_fkey
            references professions
            on delete cascade,
    constraint workers_professions_pkey
        primary key (workerid, professionid)
);

create table if not exists reviews
(
    reviewid bigint default nextval('reviews_reviewid_seq'::regclass) not null
        constraint reviews_pkey
            primary key,
    workerid bigint
        constraint reviews_workerid_fkey
            references users
            on delete cascade,
    userid bigint
        constraint reviews_userid_fkey
            references users
            on delete cascade,
    rating real,
    review varchar(255),
    date timestamp not null
);

create table if not exists times
(
    timeid bigint default nextval('times_timeid_seq'::regclass) not null
        constraint times_pkey
            primary key,
    timeinterval time
        constraint times_timeinterval_key
            unique
);

create table if not exists events
(
    eventid bigint default nextval('events_eventid_seq'::regclass) not null
        constraint events_pkey
            primary key,
    name varchar(255) not null,
    description text,
    date date not null,
    neighborhoodid bigint not null
        constraint events_neighborhoodid_fkey
            references neighborhoods
            on delete cascade,
    starttimeid bigint
        constraint fk_starttime
            references times
            on delete cascade,
    endtimeid bigint
        constraint fk_endtime
            references times
            on delete cascade
);

create table if not exists events_users
(
    userid bigint not null
        constraint events_users_userid_fkey
            references users
            on delete cascade,
    eventid bigint not null
        constraint events_users_eventid_fkey
            references events
            on delete cascade,
    constraint events_users_pkey
        primary key (eventid, userid)
);

create table if not exists days
(
    dayid bigint default nextval('days_dayid_seq'::regclass) not null
        constraint days_pkey
            primary key,
    dayname varchar(20)
        constraint days_dayname_key
            unique
);

create table if not exists shifts
(
    shiftid bigint default nextval('shifts_shiftid_seq'::regclass) not null
        constraint shifts_pkey
            primary key,
    dayid bigint
        constraint shifts_dayid_fkey
            references days,
    starttime bigint
        constraint shifts_starttime_fkey
            references times,
    constraint shifts_starttime_dayid_key
        unique (starttime, dayid)
);

create table if not exists amenities_shifts_availability
(
    amenityavailabilityid bigint default nextval('amenities_shifts_availability_amenityavailabilityid_seq'::regclass) not null
        constraint amenities_shifts_availability_pkey
            primary key,
    amenityid bigint
        constraint amenities_shifts_availability_amenityid_fkey
            references amenities
            on delete cascade,
    shiftid bigint
        constraint amenities_shifts_availability_shiftid_fkey
            references shifts
            on delete cascade,
    constraint amenities_shifts_availability_amenityid_shiftid_key
        unique (amenityid, shiftid)
);

create table if not exists users_availability
(
    bookingid bigint default nextval('users_availability_bookingid_seq'::regclass) not null
        constraint users_availability_pkey
            primary key,
    amenityavailabilityid bigint
        constraint users_availability_amenityavailabilityid_fkey
            references amenities_shifts_availability
            on delete cascade,
    userid bigint
        constraint users_availability_userid_fkey
            references users
            on delete cascade,
    date date,
    constraint users_availability_amenityavailabilityid_date_key
        unique (amenityavailabilityid, date)
);

create table if not exists departments
(
    departmentid bigint default nextval('departments_departmentid_seq'::regclass) not null
        constraint departments_pkey
            primary key,
    department varchar(64) not null
        constraint departments_department_key
            unique
);

create table if not exists products
(
    productid bigint default nextval('products_productid_seq'::regclass) not null
        constraint products_pkey
            primary key,
    name varchar(128) not null,
    description varchar(1000) not null,
    price double precision not null,
    used boolean not null,
    primarypictureid bigint
        constraint products_primarypictureid_fkey
            references images
            on delete cascade,
    secondarypictureid bigint
        constraint products_secondarypictureid_fkey
            references images
            on delete cascade,
    tertiarypictureid bigint
        constraint products_tertiarypictureid_fkey
            references images
            on delete cascade,
    sellerid bigint not null
        constraint products_sellerid_fkey
            references users
            on delete cascade,
    departmentid bigint
        constraint products_departmentid_fkey
            references departments
            on delete cascade,
    creationdate timestamp,
    remainingunits bigint default 1,
    purchasedate date
);

create table if not exists products_users_inquiries
(
    inquiryid bigint default nextval('products_users_inquiries_inquiryid_seq'::regclass) not null
        constraint products_users_inquiries_pkey
            primary key,
    productid bigint
        constraint products_users_inquiries_productid_fkey
            references products
            on delete cascade,
    userid bigint
        constraint products_users_inquiries_userid_fkey
            references users
            on delete cascade,
    message varchar(512) not null,
    reply varchar(512),
    inquirydate timestamp
);

create table if not exists products_users_requests
(
    requestid bigint default nextval('products_users_requests_requestid_seq'::regclass) not null
        constraint products_users_requests_pkey
            primary key,
    productid bigint
        constraint products_users_requests_productid_fkey
            references products
            on delete cascade,
    userid bigint
        constraint products_users_requests_userid_fkey
            references users
            on delete cascade,
    message varchar(512) not null,
    requestdate timestamp,
    purchasedate timestamp,
    units integer,
    status varchar(30)
);

create table if not exists neighborhoods_tags
(
    neighborhoodid integer not null
        constraint neighborhoods_tags_neighborhoodid_fkey
            references neighborhoods
            on delete cascade,
    tagid integer not null
        constraint neighborhoods_tags_tagid_fkey
            references tags
            on delete cascade,
    constraint neighborhoods_tags_pkey
        primary key (neighborhoodid, tagid)
);


-- Insert neighborhoods
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (-2, 'Super Admin Neighborhood', true)
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (-1, 'Rejected', true)
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (0, 'Worker Neighborhood', true)
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (1, 'Olivos Golf Club', false)
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (2, 'Pacheco Golf', false)
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname, isbase)
VALUES (3, 'Martindale', false)
ON CONFLICT DO NOTHING;

-- Insert channels
INSERT INTO channels (channelid, channel, isbase)
VALUES (1, 'Announcements', true)
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel, isbase)
VALUES (2, 'Complaints', true)
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel, isbase)
VALUES (3, 'Feed', true)
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel, isbase)
VALUES (4, 'Workers', true)
ON CONFLICT DO NOTHING;

INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (0, 4) ON CONFLICT DO NOTHING;

INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 3) ON CONFLICT DO NOTHING;

INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 3) ON CONFLICT DO NOTHING;

INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 3) ON CONFLICT DO NOTHING;

-- Inserting Departments if not exists
INSERT INTO departments (departmentid, department)
VALUES (1, 'ELECTRONICS')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (2, 'APPLIANCES')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (3, 'HOME_FURNITURE')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (4, 'CLOTHING_FASHION')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (5, 'HEALTH_BEAUTY')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (6, 'SPORTS_OUTDOORS')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (7, 'BOOKS_MEDIA')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (8, 'TOYS_GAMES')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (9, 'JEWELRY_ACCESSORIES')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (10, 'AUTOMOTIVE')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (11, 'GROCERIES_FOOD')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (12, 'PETS_SUPPLIES')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (13, 'HOME_IMPROVEMENT')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (14, 'GARDEN_OUTDOOR')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (15, 'OFFICE_SUPPLIES')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (16, 'BABY_KIDS')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (17, 'ARTS_CRAFTS')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (18, 'TRAVEL_LUGGAGE')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (19, 'MUSIC_INSTRUMENTS')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (20, 'TECHNOLOGY')
ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department)
VALUES (21, 'OTHER')
ON CONFLICT (departmentid) DO NOTHING;

-- Inserting Professions if not exists
INSERT INTO professions (professionid, profession)
VALUES (1, 'PLUMBER')
ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession)
VALUES (2, 'ELECTRICIAN')
ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession)
VALUES (3, 'POOL_MAINTENANCE')
ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession)
VALUES (4, 'GARDENER')
ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession)
VALUES (5, 'CARPENTER')
ON CONFLICT (professionid) DO NOTHING;

-- Manually inserting 24 hours
INSERT INTO times (timeid, timeinterval)
VALUES (1, '00:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (2, '01:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (3, '02:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (4, '03:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (5, '04:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (6, '05:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (7, '06:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (8, '07:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (9, '08:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (10, '09:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (11, '10:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (12, '11:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (13, '12:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (14, '13:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (15, '14:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (16, '15:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (17, '16:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (18, '17:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (19, '18:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (20, '19:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (21, '20:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (22, '21:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (23, '22:00:00')
ON CONFLICT (timeid) DO NOTHING;
INSERT INTO times (timeid, timeinterval)
VALUES (24, '23:00:00')
ON CONFLICT (timeid) DO NOTHING;


-- Populate the "days" table with the seven days of the week
INSERT INTO days (dayId, dayName)
VALUES (1, 'Monday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (2, 'Tuesday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (3, 'Wednesday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (4, 'Thursday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (5, 'Friday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (6, 'Saturday')
ON CONFLICT (dayid) DO NOTHING;
INSERT INTO days (dayId, dayName)
VALUES (7, 'Sunday')
ON CONFLICT (dayid) DO NOTHING;

-- Populate Users

insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (1, 'admin@test.com', 'Administrator', 'Tester', '2024-07-29 20:35:10.858422', 1, '$2a$10$Nm/ooz9u7QIeMY4SwFtlROdphgDnH9ez0JcQyeDPWJio6PqHTzR4K', false, 'ENGLISH', 'ADMINISTRATOR', null, 1, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (2, 'admin2@test.com', 'Administrator', 'Tester', '2024-07-29 20:35:10.859353', 2, '$2a$10$oEcSbZRjVa1K/3zXlNWAPuRZkqVmn1hJr2Z5RO7ZZByZqwHXhobuu', false, 'ENGLISH', 'ADMINISTRATOR', null, 2, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (3, 'admin3@test.com', 'Administrator', 'Tester', '2024-07-29 20:35:10.859911', 3, '$2a$10$TUmHdE0xP4PCAklMnULXAusGEqDlhOp9liBbBBj7wv8Bu9Emzs1aa', false, 'ENGLISH', 'ADMINISTRATOR', null, 3, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (4, 'verified@test.com', 'Verified', 'Tester', '2024-07-29 20:35:10.860441', 1, '$2a$10$AUfasTu1ntiaxPHNNMzIx.mF9.pzyvLR1QduRJPl723cgTk5gI9KO', false, 'ENGLISH', 'NEIGHBOR', null, 4, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (5, 'worker@test.com', 'Worker', 'Tester', '2024-07-29 20:35:10.860957', 0,'$2a$10$vqk.fb0cifAqdxUS4aUj8ukhDxgQ2nR9i.ALbUMVOt92UolFQV73C', false, 'ENGLISH', 'WORKER', null, 5, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (6, 'otherworker@test.com', 'Other Worker', 'Tester', '2024-07-29 20:39:11.070000', 0, '$2a$10$DNM3yuoFKnaMC3SH30QHAuvfljYxjyONMcTdVN4LY.6SY3c6gvMCW', false, 'ENGLISH', 'WORKER', null, 11, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (7, 'rejected@test.com', 'Rejected', 'Tester', '2024-07-29 20:39:10.542000', -1, '$2a$10$ynI2yX9gmO9B2zCvyblp4u7YfprsosxdBbGiZC2s87scJKYOLcrK.', false, 'ENGLISH', 'REJECTED', null, 6, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (8, 'otherrejected@test.com', 'Other Rejected', 'Tester', '2024-07-29 20:39:10.685000', -1, '$2a$10$1U0PpkrVDL8k2nHcFboUFuPKL9jbJU.ZWMsq4lmZX6pmyHB5SkIn2', false, 'ENGLISH', 'REJECTED', null, 7, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (9, 'unverified@test.com', 'Unverified', 'Tester', '2024-07-29 20:39:10.783000', 1, '$2a$10$06dguI61v1bNEy6QQyg2nu9LA7/MrhF7ZihXTR32bl5ck3VgECqQi', false, 'ENGLISH', 'UNVERIFIED_NEIGHBOR', null, 8, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (10, 'otherunverified@test.com', 'Other Unverified', 'Tester', '2024-07-29 20:39:10.877000', 1, '$2a$10$hcw16.2M3R/Lydnk8QAT0e56Yz6CW3y0jgH9SoP3UGLRONQ8tdlhO', false, 'ENGLISH', 'UNVERIFIED_NEIGHBOR', null, 9, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (11, 'otherverified@test.com', 'Other Verified', 'Tester', '2024-07-29 20:39:10.970000', 1, '$2a$10$.4nQNa7Y3CA0AGI7X9mwTO37AQaWHeho0.CQR7Mt4p6Du5yQK0WrW', false, 'ENGLISH', 'NEIGHBOR', null, 10, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (12, 'superadmin@test.com', 'Super Admin', 'Tester', '2024-07-29 20:39:11.179000', -2, '$2a$10$8LZMiGzU2gC/ZkBPeObbv.ZtpjfJ29VEJSRutI3EdDGUWpMG2vShS', false, 'ENGLISH', 'SUPER_ADMINISTRATOR', null, 12, null) ON CONFLICT DO NOTHING;
insert into users (userid, mail, name, surname, creationdate, neighborhoodid, password, darkmode, language, role, profilepictureid, identification, phonenumber) values (13, 'otherotherverified@test.com', 'Other Other Verified', 'Tester', '2024-07-31 12:05:44.241000', 1, '$2a$10$Kszxn6RQ.Qm8w0ep90xiU.qWy.UX7UsrDHhYfNCxHVh7R4p349qLy', false, 'ENGLISH', 'NEIGHBOR', null, 13, null) ON CONFLICT DO NOTHING;

INSERT INTO workers_info (workerid, address, backgroundpictureid, bio, businessname, phonenumber)
VALUES (5, 'Wherever', null, 'Best in Town', 'Fix it All', '1231231')
ON CONFLICT DO NOTHING;

INSERT INTO workers_info (workerid, address, backgroundpictureid, bio, businessname, phonenumber)
VALUES (6, 'Wherever I Wanna Be', null, 'Second Best in Town', 'Fix it Almost All', '43243847')
ON CONFLICT DO NOTHING;

insert into shifts (dayid, starttime) values (1, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (1, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (2, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (3, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (4, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (5, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (6, 24) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 1) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 2) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 3) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 4) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 5) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 6) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 7) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 8) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 9) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 10) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 11) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 12) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 13) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 14) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 15) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 16) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 17) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 18) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 19) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 20) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 21) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 22) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 23) ON CONFLICT DO NOTHING;
insert into shifts (dayid, starttime) values (7, 24) ON CONFLICT DO NOTHING;

-- Adjusting sequences for neighborhoods
SELECT setval('neighborhoods_neighborhoodid_seq', (SELECT MAX(neighborhoodid) FROM neighborhoods));

-- Adjusting sequences for channels
SELECT setval('channels_channelid_seq', (SELECT MAX(channelid) FROM channels));

-- Adjusting sequences for departments
SELECT setval('departments_departmentid_seq', (SELECT MAX(departmentid) FROM departments));

-- Adjusting sequences for professions
SELECT setval('professions_professionid_seq', (SELECT MAX(professionid) FROM professions));

-- Adjusting sequences for users
SELECT setval('users_userid_seq', (SELECT MAX(userid) FROM users));

-- Update the sequence for times
SELECT setval('times_timeid_seq', (SELECT MAX(timeid) FROM times));

-- Update the sequence for days
SELECT setval('days_dayid_seq', (SELECT MAX(dayid) FROM days));