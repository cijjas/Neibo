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

create table if not exists neighborhoods
(
    neighborhoodid bigint default nextval('neighborhoods_neighborhoodid_seq'::regclass) not null
        constraint neighborhoods_pkey
            primary key,
    neighborhoodname varchar(128)
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
            unique
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
            on delete cascade
        constraint fkic88b519i3tqt5e0q3vkf3y5t
            references neighborhoods,
    password varchar(128),
    darkmode boolean,
    language varchar(32),
    role varchar(64),
    profilepictureid bigint
        constraint fk_users_images
            references images
            on delete set null
        constraint fk1eepsnt7x3dn9bcluxeecrg34
            references images,
    identification integer,
    phonenumber varchar(64)
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
            on delete cascade
        constraint fktc10cvjiaj3p7ldl526coc36a
            references users,
    channelid bigint not null
        constraint posts_channelid_fkey
            references channels
            on delete cascade
        constraint fkqn2vwdbbedu2lmm3mi5n4vjnq
            references channels,
    postpictureid bigint
        constraint fk_posts_images
            references images
            on delete set null
        constraint fk9oso7y3hmscxyahy12fspfvyx
            references images
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
            on delete cascade
        constraint fkjxggc60wwwlf4xl065fjrx68y
            references users,
    postid bigint not null
        constraint comments_postid_fkey
            references posts
            on delete cascade
        constraint fkqt8anaen7vlhry2a766wkvv41
            references posts
);

create table if not exists posts_tags
(
    postid bigint not null
        constraint posts_tags_postid_fkey
            references posts
            on delete cascade
        constraint fk9d2rjjmbiureptqp0hn4wfd93
            references posts,
    tagid bigint not null
        constraint posts_tags_tagid_fkey
            references tags
            on delete cascade
        constraint fkp7fqkfledrnb5vph2cumrgwg4
            references tags,
    constraint posts_tags_pkey
        primary key (postid, tagid)
);

create table if not exists neighborhoods_channels
(
    neighborhoodid bigint not null
        constraint neighborhoods_channels_neighborhoodid_fkey
            references neighborhoods
            on delete cascade
        constraint fkhtu9bmy29n0d43wc59dt6jvr5
            references neighborhoods,
    channelid bigint not null
        constraint neighborhoods_channels_channelid_fkey
            references channels
            on delete cascade
        constraint fk5v7ohayjrcs4l9xfvulc1id3g
            references channels,
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
            references images
        constraint fk8u7gju00jdbcp3ejyrab6uud5
            references images,
    neighborhoodid bigint not null
        constraint resources_neighborhoodid_fkey
            references neighborhoods
        constraint fkm326w3rerctvghpu8yct39glk
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
        constraint fklux0wry30t2vlbwyv6nwm4pv6
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
        constraint fks3gyc5psipkyoeidl506ypor
            references neighborhoods
);

create table if not exists posts_users_likes
(
    postid bigint not null
        constraint posts_users_likes_postid_fkey
            references posts
            on delete cascade
        constraint fk2v3c2g4kqvmjbyva0y9n261ps
            references posts,
    userid bigint not null
        constraint posts_users_likes_userid_fkey
            references users
            on delete cascade
        constraint fk1phwqt3hh8wcip5hcoxct3ax5
            references users,
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
    backgroundpictureid bigint,
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
            on delete cascade
        constraint fkop2knb49048w039sru9wl7m4v
            references workers_info,
    neighborhoodid bigint not null
        constraint workers_neighborhoods_neighborhoodid_fkey
            references neighborhoods
            on delete cascade
        constraint fklyb81uy312pcm3bqlygtudqac
            references neighborhoods,
    role varchar(255),
    constraint workers_neighborhoods_pkey
        primary key (workerid, neighborhoodid)
);

create table if not exists workers_professions
(
    workerid bigint not null
        constraint workers_professions_workerid_fkey
            references users
            on delete cascade
        constraint fkf1a7cka14k4ty63gra57x2vwe
            references workers_info,
    professionid bigint not null
        constraint workers_professions_professionid_fkey
            references professions
            on delete cascade
        constraint fkhihhwwcf8km97mlf13r0vqn
            references professions,
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
            on delete cascade
        constraint fkd87kbnxrdtjriixleh5wxy3xi
            references workers_info,
    userid bigint
        constraint reviews_userid_fkey
            references users
            on delete cascade
        constraint fke0hlob2fbf7wug4lgi2boiyxf
            references users,
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
            on delete cascade
        constraint fk37hdki702o2bv4r3t41i7yk4o
            references neighborhoods,
    starttimeid bigint
        constraint fk_starttime
            references times
            on delete cascade
        constraint fkab8xrhqn8m4poq2xvqt6l5v72
            references times,
    endtimeid bigint
        constraint fk_endtime
            references times
            on delete cascade
        constraint fk4y7lfu97tj38vccdl00uwpn6t
            references times
);

create table if not exists events_users
(
    userid bigint not null
        constraint events_users_userid_fkey
            references users
            on delete cascade
        constraint fk84k1rf46d33arjqggmbj0lyjy
            references users,
    eventid bigint not null
        constraint events_users_eventid_fkey
            references events
            on delete cascade
        constraint fkll2jkxq61gb2cn8c0wgw1lkfq
            references events,
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
            references days
        constraint fkrgr216xwoagaxrpo0v757c881
            references days,
    starttime bigint
        constraint shifts_starttime_fkey
            references times
        constraint fk5nflqno4eofi5n2onehgbu6tt
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
            on delete cascade
        constraint fks3qffu9r6rxbo6ffxt3wy679l
            references amenities,
    shiftid bigint
        constraint amenities_shifts_availability_shiftid_fkey
            references shifts
            on delete cascade
        constraint fk3mdnowlxv760ced06okgsy8rh
            references shifts,
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
            on delete cascade
        constraint fks9a957rfgy3db1dben7xnj0u1
            references amenities_shifts_availability,
    userid bigint
        constraint users_availability_userid_fkey
            references users
            on delete cascade
        constraint fk2fygq11yidgx548rnvmb3kp9e
            references users,
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
            on delete cascade
        constraint fkes5pp3fnwoaewjtkpkh8mayxd
            references images,
    secondarypictureid bigint
        constraint products_secondarypictureid_fkey
            references images
            on delete cascade
        constraint fkjh0vklu17y1qmlifr3ckbnsn7
            references images,
    tertiarypictureid bigint
        constraint products_tertiarypictureid_fkey
            references images
            on delete cascade
        constraint fkcj69ibo77igk2yxx9yyarn609
            references images,
    sellerid bigint not null
        constraint products_sellerid_fkey
            references users
            on delete cascade
        constraint fk7ghqa830fextpfnt6qw4dvly1
            references users,
    departmentid bigint
        constraint products_departmentid_fkey
            references departments
            on delete cascade
        constraint fk8iyjjwe05ysnrnwemuwlk8nqr
            references departments,
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
            on delete cascade
        constraint fkqxpb7oab9phj2hsbiw4trel5l
            references products,
    userid bigint
        constraint products_users_inquiries_userid_fkey
            references users
            on delete cascade
        constraint fk6ptiod8ihfdhv07m04jhcvb3e
            references users,
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
            on delete cascade
        constraint fk3rojkgclaljwd3vr0qa8qbsmc
            references products,
    userid bigint
        constraint products_users_requests_userid_fkey
            references users
            on delete cascade
        constraint fkt3dd1mkdokg6anp41w64y397t
            references users,
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
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (-2, 'Super Admin Neighborhood')
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (-1, 'Rejected')
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (0, 'Worker Neighborhood')
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (1, 'Olivos Golf Club')
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (2, 'Pacheco Golf')
ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname)
VALUES (3, 'Martindale')
ON CONFLICT DO NOTHING;

-- Insert channels
INSERT INTO channels (channelid, channel)
VALUES (1, 'Announcements')
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel)
VALUES (2, 'Complaints')
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel)
VALUES (3, 'Feed')
ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel)
VALUES (4, 'Workers')
ON CONFLICT DO NOTHING;

INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 3) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (1, 4) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 3) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (2, 4) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 1) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 2) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 3) ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods_channels(neighborhoodid, channelid)
VALUES (3, 4) ON CONFLICT DO NOTHING;

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
INSERT INTO departments (departmentid, department)
VALUES (22, 'NONE')
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

insert into shifts (shiftid, dayid, starttime) values (1, 1, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (2, 1, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (3, 1, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (4, 1, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (5, 1, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (6, 1, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (7, 1, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (8, 1, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (9, 1, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (10, 1, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (11, 1, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (12, 1, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (13, 1, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (14, 1, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (15, 1, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (16, 1, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (17, 1, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (18, 1, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (19, 1, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (20, 1, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (21, 1, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (22, 1, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (23, 1, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (24, 1, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (25, 2, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (26, 2, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (27, 2, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (28, 2, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (29, 2, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (30, 2, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (31, 2, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (32, 2, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (33, 2, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (34, 2, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (35, 2, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (36, 2, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (37, 2, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (38, 2, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (39, 2, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (40, 2, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (41, 2, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (42, 2, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (43, 2, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (44, 2, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (45, 2, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (46, 2, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (47, 2, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (48, 2, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (49, 3, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (50, 3, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (51, 3, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (52, 3, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (53, 3, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (54, 3, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (55, 3, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (56, 3, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (57, 3, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (58, 3, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (59, 3, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (60, 3, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (61, 3, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (62, 3, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (63, 3, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (64, 3, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (65, 3, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (66, 3, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (67, 3, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (68, 3, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (69, 3, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (70, 3, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (71, 3, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (72, 3, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (73, 4, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (74, 4, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (75, 4, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (76, 4, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (77, 4, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (78, 4, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (79, 4, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (80, 4, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (81, 4, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (82, 4, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (83, 4, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (84, 4, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (85, 4, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (86, 4, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (87, 4, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (88, 4, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (89, 4, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (90, 4, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (91, 4, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (92, 4, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (93, 4, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (94, 4, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (95, 4, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (96, 4, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (97, 5, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (98, 5, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (99, 5, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (100, 5, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (101, 5, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (102, 5, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (103, 5, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (104, 5, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (105, 5, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (106, 5, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (107, 5, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (108, 5, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (109, 5, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (110, 5, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (111, 5, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (112, 5, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (113, 5, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (114, 5, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (115, 5, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (116, 5, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (117, 5, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (118, 5, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (119, 5, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (120, 5, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (121, 6, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (122, 6, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (123, 6, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (124, 6, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (125, 6, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (126, 6, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (127, 6, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (128, 6, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (129, 6, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (130, 6, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (131, 6, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (132, 6, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (133, 6, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (134, 6, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (135, 6, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (136, 6, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (137, 6, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (138, 6, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (139, 6, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (140, 6, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (141, 6, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (142, 6, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (143, 6, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (144, 6, 24) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (145, 7, 1) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (146, 7, 2) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (147, 7, 3) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (148, 7, 4) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (149, 7, 5) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (150, 7, 6) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (151, 7, 7) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (152, 7, 8) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (153, 7, 9) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (154, 7, 10) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (155, 7, 11) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (156, 7, 12) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (157, 7, 13) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (158, 7, 14) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (159, 7, 15) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (160, 7, 16) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (161, 7, 17) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (162, 7, 18) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (163, 7, 19) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (164, 7, 20) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (165, 7, 21) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (166, 7, 22) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (167, 7, 23) ON CONFLICT DO NOTHING;
insert into shifts (shiftid, dayid, starttime) values (168, 7, 24) ON CONFLICT DO NOTHING;


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