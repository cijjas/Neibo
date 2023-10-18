SET
DATABASE SQL SYNTAX PGS TRUE;
-- Clean values from all tables
DELETE
FROM users_availability;
DELETE
FROM amenities_shifts_availability;
DELETE
FROM images;
DELETE
FROM comments;
DELETE
FROM contacts;
DELETE
FROM posts_users_subscriptions;
DELETE
FROM posts_users_likes;
DELETE
FROM posts_tags;
DELETE
FROM posts;
DELETE
FROM tags;
DELETE
FROM neighborhoods_channels;
DELETE
FROM amenities;
DELETE
FROM users;
DELETE
FROM resources;
DELETE
FROM channels;
DELETE
FROM neighborhoods;

-- Clean values from tables related to workers
DELETE
FROM workers_info;
DELETE
FROM workers_professions;
DELETE
FROM professions;
DELETE
FROM workers_neighborhoods;
DELETE
FROM reviews;

-- Clean values from tables related to amenity refactor
DELETE
FROM shifts;
DELETE
FROM times;
DELETE
FROM days;

DELETE
FROM events_users;
DELETE
FROM events;

