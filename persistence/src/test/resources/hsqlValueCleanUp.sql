SET DATABASE SQL SYNTAX PGS TRUE;

-- Clean values from all tables
DELETE FROM events_users;
DELETE FROM events;
DELETE FROM comments;
DELETE FROM contacts;
DELETE FROM posts_users_subscriptions;
DELETE FROM posts_users_likes;
DELETE FROM posts_tags;
DELETE FROM posts;
DELETE FROM tags;
DELETE FROM neighborhoods_channels;
DELETE FROM amenities_hours;
DELETE FROM amenities;
DELETE FROM users;
DELETE FROM resources;
DELETE FROM reservations;
DELETE FROM hours;
DELETE FROM images;
DELETE FROM channels;
DELETE FROM neighborhoods;

-- Insert values into tables
-- (Insert the same values as provided in your previous script)
