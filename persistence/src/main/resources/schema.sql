------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------- GENERATION -------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
/*
-- HIBERNATE TAKES CARE OF THIS NOW

create table if not exists neighborhoods
(
    neighborhoodid   serial
    primary key,
    neighborhoodname varchar(128)
    );

create table if not exists tags
(
    tagid serial
    primary key,
    tag   varchar(64) not null
    unique
    );

create table if not exists channels
(
    channelid serial
    primary key,
    channel   varchar(64) not null
    unique
    );

create table if not exists images
(
    imageid serial
    primary key,
    image   bytea not null
);

create table if not exists users
(
    userid           serial
    primary key,
    mail             varchar(128) not null
    unique,
    name             varchar(64)  not null,
    surname          varchar(64)  not null,
    creationdate     timestamp    not null,
    neighborhoodid   integer      not null
    references neighborhoods
    on delete cascade
    constraint fkic88b519i3tqt5e0q3vkf3y5t
    references neighborhoods,
    password         varchar(128),
    darkmode         boolean,
    language         varchar(32),
    role             varchar(64),
    profilepictureid integer
    constraint fk_users_images
    references images
    on delete set null
    constraint fk1eepsnt7x3dn9bcluxeecrg34
    references images,
    identification   integer,
    phonenumber      varchar(64)
    );

create table if not exists posts
(
    postid        serial
    primary key,
    title         varchar(128) not null,
    description   text         not null,
    postdate      timestamp    not null,
    userid        integer      not null
    constraint fk_posts_users
    references users
    on delete cascade
    constraint fktc10cvjiaj3p7ldl526coc36a
    references users,
    channelid     integer      not null
    references channels
    on delete cascade
    constraint fkqn2vwdbbedu2lmm3mi5n4vjnq
    references channels,
    postpictureid integer
    constraint fk_posts_images
    references images
    on delete set null
    constraint fk9oso7y3hmscxyahy12fspfvyx
    references images
    );

create table if not exists comments
(
    commentid   serial
    primary key,
    comment     varchar(512) not null,
    commentdate date         not null,
    userid      integer      not null
    constraint fk_comments_users
    references users
    on delete cascade
    constraint fkjxggc60wwwlf4xl065fjrx68y
    references users,
    postid      integer      not null
    references posts
    on delete cascade
    constraint fkqt8anaen7vlhry2a766wkvv41
    references posts
    );

create table if not exists posts_tags
(
    postid integer not null
    references posts
    on delete cascade
    constraint fk9d2rjjmbiureptqp0hn4wfd93
    references posts,
    tagid  integer not null
    references tags
    on delete cascade
    constraint fkp7fqkfledrnb5vph2cumrgwg4
    references tags,
    primary key (postid, tagid)
    );

create table if not exists neighborhoods_channels
(
    neighborhoodid integer not null
    references neighborhoods
    on delete cascade
    constraint fkhtu9bmy29n0d43wc59dt6jvr5
    references neighborhoods,
    channelid      integer not null
    references channels
    on delete cascade
    constraint fk5v7ohayjrcs4l9xfvulc1id3g
    references channels,
    primary key (neighborhoodid, channelid)
    );

create table if not exists resources
(
    resourceid          serial
    primary key,
    resourcetitle       varchar(64),
    resourcedescription varchar(255),
    resourceimageid     integer
    references images
    constraint fk8u7gju00jdbcp3ejyrab6uud5
    references images,
    neighborhoodid      integer not null
    references neighborhoods
    constraint fkm326w3rerctvghpu8yct39glk
    references neighborhoods
    );

create table if not exists contacts
(
    contactid      serial
    primary key,
    contactname    varchar(64) not null,
    contactaddress varchar(128),
    contactphone   varchar(32),
    neighborhoodid integer     not null
    references neighborhoods
    constraint fklux0wry30t2vlbwyv6nwm4pv6
    references neighborhoods
    );

create table if not exists amenities
(
    amenityid      serial
    primary key,
    name           varchar(512) not null,
    description    varchar(512) not null,
    neighborhoodid integer      not null
    references neighborhoods
    on delete cascade
    constraint fks3gyc5psipkyoeidl506ypor
    references neighborhoods
    );

create table if not exists reservations
(
    reservationid serial
    primary key,
    date          date    not null,
    starttime     time    not null,
    endtime       time    not null,
    userid        integer not null
    references users
    on delete cascade,
    amenityid     integer not null
    references amenities
    on delete cascade
);

create table if not exists posts_users_likes
(
    postid   integer not null
    references posts
    on delete cascade
    constraint fk2v3c2g4kqvmjbyva0y9n261ps
    references posts,
    userid   integer not null
    references users
    on delete cascade
    constraint fk1phwqt3hh8wcip5hcoxct3ax5
    references users,
    likedate timestamp default CURRENT_TIMESTAMP,
    primary key (postid, userid)
    );

create table if not exists workers_info
(
    workerid            integer      not null
    primary key
    references users
    on delete cascade,
    phonenumber         varchar(64)  not null,
    businessname        varchar(128),
    address             varchar(128) not null,
    backgroundpictureid integer,
    bio                 varchar(255)
    );

create table if not exists professions
(
    professionid serial
    primary key,
    profession   varchar(64) not null
    unique
    );

create table if not exists workers_neighborhoods
(
    workerid       integer not null
    references users
    on delete cascade
    constraint fkop2knb49048w039sru9wl7m4v
    references workers_info,
    neighborhoodid integer not null
    references neighborhoods
    on delete cascade
    constraint fklyb81uy312pcm3bqlygtudqac
    references neighborhoods,
    role           text,
    primary key (workerid, neighborhoodid)
    );

create table if not exists workers_professions
(
    workerid     integer not null
    references users
    on delete cascade
    constraint fkf1a7cka14k4ty63gra57x2vwe
    references workers_info,
    professionid integer not null
    references professions
    on delete cascade
    constraint fkhihhwwcf8km97mlf13r0vqn
    references professions,
    primary key (workerid, professionid)
    );

create table if not exists reviews
(
    reviewid serial
    primary key,
    workerid integer
    references users
    on delete cascade
    constraint fkd87kbnxrdtjriixleh5wxy3xi
    references workers_info,
    userid   integer
    references users
    on delete cascade
    constraint fke0hlob2fbf7wug4lgi2boiyxf
    references users,
    rating   double precision,
    review   varchar(255),
    date     timestamp not null
    );

create table if not exists times
(
    timeid       serial
    primary key,
    timeinterval time
    unique
);

create table if not exists events
(
    eventid        serial
    primary key,
    name           varchar(255) not null,
    description    text,
    date           date         not null,
    neighborhoodid integer      not null
    references neighborhoods
    on delete cascade
    constraint fk37hdki702o2bv4r3t41i7yk4o
    references neighborhoods,
    starttimeid    integer
    constraint fk_starttime
    references times
    on delete cascade
    constraint fkab8xrhqn8m4poq2xvqt6l5v72
    references times,
    endtimeid      integer
    constraint fk_endtime
    references times
    on delete cascade
    constraint fk4y7lfu97tj38vccdl00uwpn6t
    references times
    );

create table if not exists events_users
(
    userid  integer not null
    references users
    on delete cascade
    constraint fk84k1rf46d33arjqggmbj0lyjy
    references users,
    eventid integer not null
    references events
    on delete cascade
    constraint fkll2jkxq61gb2cn8c0wgw1lkfq
    references events,
    primary key (eventid, userid)
    );

create table if not exists days
(
    dayid   serial
    primary key,
    dayname varchar(20)
    unique
    );

create table if not exists shifts
(
    shiftid   serial
    primary key,
    dayid     integer
    references days
    constraint fkrgr216xwoagaxrpo0v757c881
    references days,
    starttime integer
    references times
    constraint fk5nflqno4eofi5n2onehgbu6tt
    references times,
    unique (starttime, dayid)
    );

create table if not exists amenities_shifts_availability
(
    amenityavailabilityid serial
    primary key,
    amenityid             integer
    references amenities
    on delete cascade
    constraint fks3qffu9r6rxbo6ffxt3wy679l
    references amenities,
    shiftid               integer
    references shifts
    on delete cascade
    constraint fk3mdnowlxv760ced06okgsy8rh
    references shifts,
    unique (amenityid, shiftid)
    );

create table if not exists users_availability
(
    bookingid             serial
    primary key,
    amenityavailabilityid integer
    references amenities_shifts_availability
    on delete cascade
    constraint fks9a957rfgy3db1dben7xnj0u1
    references amenities_shifts_availability,
    userid                integer
    references users
    on delete cascade
    constraint fk2fygq11yidgx548rnvmb3kp9e
    references users,
    date                  date,
    unique (amenityavailabilityid, date)
    );

create table if not exists departments
(
    departmentid integer default nextval('departments_departmentid_seq1'::regclass) not null
    primary key,
    department   varchar(64)                                                        not null
    unique
    );

create table if not exists products
(
    productid          integer default nextval('products_productid_seq1'::regclass) not null
    primary key,
    name               varchar(128)                                                 not null,
    description        varchar(1000)                                                not null,
    price              double precision                                             not null,
    used               boolean                                                      not null,
    primarypictureid   integer
    references images
    on delete cascade
    constraint fkes5pp3fnwoaewjtkpkh8mayxd
    references images,
    secondarypictureid integer
    references images
    on delete cascade
    constraint fkjh0vklu17y1qmlifr3ckbnsn7
    references images,
    tertiarypictureid  integer
    references images
    on delete cascade
    constraint fkcj69ibo77igk2yxx9yyarn609
    references images,
    sellerid           integer                                                      not null
    references users
    on delete cascade
    constraint fk7ghqa830fextpfnt6qw4dvly1
    references users,
    departmentid       integer
    references departments
    on delete cascade
    constraint fk8iyjjwe05ysnrnwemuwlk8nqr
    references departments,
    creationdate       date,
    remainingunits     integer default 1
    );

create table if not exists products_users_inquiries
(
    inquiryid integer default nextval('products_users_inquiries_inquiryid_seq1'::regclass) not null
    primary key,
    productid integer
    references products
    on delete cascade
    constraint fkqxpb7oab9phj2hsbiw4trel5l
    references products,
    userid    integer
    references users
    on delete cascade
    constraint fk6ptiod8ihfdhv07m04jhcvb3e
    references users,
    message   varchar(512)                                                                 not null,
    reply     varchar(512)
    );

create table if not exists products_users_requests
(
    requestid   integer default nextval('products_users_requests_requestid_seq1'::regclass) not null
    primary key,
    productid   integer
    references products
    on delete cascade
    constraint fk3rojkgclaljwd3vr0qa8qbsmc
    references products,
    userid      integer
    references users
    on delete cascade
    constraint fkt3dd1mkdokg6anp41w64y397t
    references users,
    message     varchar(512)                                                                not null,
    requestdate date
    );

create table if not exists products_users_purchases
(
    purchaseid   serial
    primary key,
    units        integer,
    productid    integer
    references products
    constraint fkj6tu1b7jvw2sgsyqkym9rof52
    references products,
    userid       integer
    references users
    constraint fkecl9mfhyuqwevvryrohsti10a
    references users,
    purchasedate timestamp
);
*/

-- Insert neighborhoods
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname) VALUES (-1, 'Rejected') ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname) VALUES (0, 'Worker Neighborhood') ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname) VALUES (1, 'Olivos Golf Club') ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname) VALUES (2, 'Pacheco Golf') ON CONFLICT DO NOTHING;
INSERT INTO neighborhoods (neighborhoodid, neighborhoodname) VALUES (3, 'Martindale') ON CONFLICT DO NOTHING;

-- Insert channels
INSERT INTO channels (channelid, channel) VALUES (1, 'Announcements') ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel) VALUES (2, 'Complaints') ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel) VALUES (3, 'Feed') ON CONFLICT DO NOTHING;
INSERT INTO channels (channelid, channel) VALUES (4, 'Workers') ON CONFLICT DO NOTHING;

-- Inserting Departments if not exists
INSERT INTO departments (departmentid, department) VALUES (1, 'ELECTRONICS') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (2, 'APPLIANCES') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (3, 'HOME_FURNITURE') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (4, 'CLOTHING_FASHION') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (5, 'HEALTH_BEAUTY') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (6, 'SPORTS_OUTDOORS') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (7, 'BOOKS_MEDIA') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (8, 'TOYS_GAMES') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (9, 'JEWELRY_ACCESSORIES') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (10, 'AUTOMOTIVE') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (11, 'GROCERIES_FOOD') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (12, 'PETS_SUPPLIES') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (13, 'HOME_IMPROVEMENT') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (14, 'GARDEN_OUTDOOR') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (15, 'OFFICE_SUPPLIES') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (16, 'BABY_KIDS') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (17, 'ARTS_CRAFTS') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (18, 'TRAVEL_LUGGAGE') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (19, 'MUSIC_INSTRUMENTS') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (20, 'TECHNOLOGY') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (21, 'OTHER') ON CONFLICT (departmentid) DO NOTHING;
INSERT INTO departments (departmentid, department) VALUES (22, 'NONE') ON CONFLICT (departmentid) DO NOTHING;

-- Inserting Professions if not exists
INSERT INTO professions (professionid, profession) VALUES (1, 'PLUMBER') ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession) VALUES (2, 'ELECTRICIAN') ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession) VALUES (3, 'POOL_MAINTENANCE') ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession) VALUES (4, 'GARDENER') ON CONFLICT (professionid) DO NOTHING;
INSERT INTO professions (professionid, profession) VALUES (5, 'CARPENTER') ON CONFLICT (professionid) DO NOTHING;

-- Populate the "times" table with 1-hour intervals from 00:00 to 23:00
INSERT INTO times (timeid, timeInterval)
SELECT
    nextval('times_timeid_seq'),
    (n || ' hours')::interval
FROM generate_series(0, 23, 1) AS n
    ON CONFLICT (timeid) DO NOTHING;

-- Populate the "days" table with the seven days of the week
INSERT INTO days (dayId, dayName)
VALUES (1,'Monday'), (2, 'Tuesday'), (3, 'Wednesday'), (4, 'Thursday'), (5, 'Friday'), (6, 'Saturday'), (7, 'Sunday')
    ON CONFLICT (dayid) DO NOTHING;

-- Populate Users
INSERT INTO users (userid, mail, name, surname, creationDate, identification, neighborhoodId, password, darkmode, language, role)  VALUES
    (1, 'admin@test.com', 'Administrator', 'Tester', CURRENT_TIMESTAMP, 1, 1, '$2a$10$Nm/ooz9u7QIeMY4SwFtlROdphgDnH9ez0JcQyeDPWJio6PqHTzR4K', false, 'ENGLISH', 'ADMINISTRATOR') ON CONFLICT DO NOTHING;

INSERT INTO users (userid, mail, name, surname, creationDate, identification, neighborhoodId, password, darkmode, language, role)  VALUES
    (2, 'admin2@test.com', 'Administrator', 'Tester', CURRENT_TIMESTAMP, 2, 2, '$2a$10$Nm/ooz9u7QIeMY4SwFtlROdphgDnH9ez0JcQyeDPWJio6PqHTzR4K', false, 'ENGLISH', 'ADMINISTRATOR') ON CONFLICT DO NOTHING;

INSERT INTO users (userid, mail, name, surname, creationDate, identification, neighborhoodId, password, darkmode, language, role)  VALUES
    (3, 'admin3@test.com', 'Administrator', 'Tester', CURRENT_TIMESTAMP, 3, 3, '$2a$10$Nm/ooz9u7QIeMY4SwFtlROdphgDnH9ez0JcQyeDPWJio6PqHTzR4K', false, 'ENGLISH', 'ADMINISTRATOR') ON CONFLICT DO NOTHING;

INSERT INTO users (userid, mail, name, surname, creationDate, identification, neighborhoodId, password, darkmode, language, role)  VALUES
    (4, 'verified@test.com', 'Verified', 'Tester', CURRENT_TIMESTAMP, 4, 1, '$2a$10$AUfasTu1ntiaxPHNNMzIx.mF9.pzyvLR1QduRJPl723cgTk5gI9KO', false, 'ENGLISH', 'NEIGHBOR') ON CONFLICT DO NOTHING;

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