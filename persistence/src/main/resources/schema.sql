/*
---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------
------------------------------------------ ¡¡¡CUIDADO!!!  -----------------------------------------
---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------


------------------------------------------ CREATION -----------------------------------------------
-- The Posts and the Neighbors are the two main entities in this schema
-- A Neighbor belongs to a Specific Neighborhood, can write comments, can post, and can be subscribed to multiple Posts to receive follow-up information
-- A Post has many specifications within it like the channel it is from and the tags it is associated with

-- Create Neighborhoods
create table if not exists neighborhoods(
    neighborhoodId SERIAL NOT NULL PRIMARY KEY, -- Unique identifier for each neighborhood
    neighborhoodName VARCHAR(128) -- Name of the neighborhood
);

-- Create Tags
-- Tags are finite, unique, and initially created by us
create table if not exists tags(
    tagId SERIAL NOT NULL PRIMARY KEY, -- Unique identifier for each tag
    tag VARCHAR(64) NOT NULL UNIQUE -- Name of the tag (must be unique)
);

-- Create Channels
-- Channels are finite, unique, and initially created by us
create table if not exists channels(
    channelId SERIAL NOT NULL PRIMARY KEY, -- Unique identifier for each tag
    channel VARCHAR(64) NOT NULL UNIQUE -- Name of the tag (must be unique)
);

-- Create Neighbors
create table if not exists neighbors(
    neighborId SERIAL PRIMARY KEY, -- Unique identifier for each user
    mail VARCHAR(128) NOT NULL UNIQUE, -- Email address of the user (must be unique)
    name VARCHAR(64) NOT NULL, -- First name of the user
    surname VARCHAR(64) NOT NULL, -- Last name of the user
    creationDate TIMESTAMP NOT NULL, -- Date when the post was created
    -- profilePicture VARCHAR(1048576), -- Only image for the profile, nullable
    neighborhoodId INT NOT NULL, -- ID of the neighborhood to which the user belongs
    foreign key (neighborhoodId) references neighborhoods(neighborhoodId) ON DELETE CASCADE -- Reference to the neighborhoods table
);

-- Create Post
create table if not exists posts(
    postId SERIAL PRIMARY KEY, -- Unique identifier for each post
    title VARCHAR(128) NOT NULL, -- Title of the post
    description TEXT NOT NULL, -- Description of the post
    postImage VARCHAR(1048576), -- Only image for the post, nullable
    postDate TIMESTAMP NOT NULL, -- Date when the post was created
    neighborId INT NOT NULL, -- ID of the user who created the post
    channelId INT NOT NULL, -- ID of the channel the post is being posted in
    foreign key (neighborId) references neighbors(neighborId) ON DELETE CASCADE, -- Reference to the neighbors table
    foreign key (channelId) references channels(channelId) ON DELETE CASCADE -- Reference to the channels table
);

-- Create Comments
create table if not exists comments(
    commentId SERIAL NOT NULL PRIMARY KEY, -- Unique identifier for each comment
    comment VARCHAR(512) NOT NULL, -- The comment text
    commentDate DATE NOT NULL, -- Date when the comment was posted

    neighborId INT NOT NULL, -- ID of the user who posted the comment
    postId INT NOT NULL, -- ID of the post to which the comment is associated
    foreign key (neighborId) references neighbors(neighborId) ON DELETE CASCADE, -- Reference to the neighbors table
    foreign key (postId) references posts(postId) ON DELETE CASCADE -- Reference to the posts table
);



------------------------------------------------------------------------------------------------------------------------

-- Create Junction Table posts_tags
create table if not exists posts_tags(
    postId INT,
    tagId INT,
    primary key (postId, tagId),
    foreign key (postId) references posts(postId) ON DELETE CASCADE, -- Reference to the posts table
    foreign key (tagId) references tags(tagId) ON DELETE CASCADE -- Reference to the tags table
);

-- Create Junction Table posts_neighbors (Subscriptions)
create table if not exists posts_neighbors(
    neighborId INT,
    postId INT,
    primary key (postId, neighborId),
    foreign key (postId) references posts(postId) ON DELETE CASCADE, -- Reference to the posts table
    foreign key (neighborId) references neighbors(neighborId) ON DELETE CASCADE -- Reference to the neighbors table
);



------------------------------------------ POPULATION -----------------------------------------------

-- Insert neighborhoods
INSERT INTO neighborhoods (neighborhoodname) VALUES ('Olivos Golf Club');
INSERT INTO neighborhoods (neighborhoodname) VALUES ('Pacheco Golf');
INSERT INTO neighborhoods (neighborhoodname) VALUES ('Martindale');

-- Insert channels
INSERT INTO channels (channel) VALUES ('Foro');
INSERT INTO channels (channel) VALUES ('Administracion');
INSERT INTO channels (channel) VALUES ('Feed');

-- Insert tags
INSERT INTO tags (tag) VALUES ('Musica');
INSERT INTO tags (tag) VALUES ('Deporte');
INSERT INTO tags (tag) VALUES ('Ocio');

-- Insert a user
INSERT INTO neighbors (mail, name, creationdate, surname, neighborhoodid) VALUES ('daddyyankee@test.com', 'Daddy', CURRENT_TIMESTAMP, 'Yankee', 1);
INSERT INTO neighbors (mail, name, creationdate, surname, neighborhoodid) VALUES ('cocochannel@test.com', 'Coco', CURRENT_TIMESTAMP, 'Channel', 2);
INSERT INTO neighbors (mail, name, creationdate, surname, neighborhoodid) VALUES ('patriciorey@test.com', 'Patricio', CURRENT_TIMESTAMP, 'Rey', 3);

-- Insert a post
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Despacito', 'Quiero nanananann paredes de tu laberinto nanananna, dame mas gasolina', CURRENT_TIMESTAMP, 1, 1);
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Pose', 'Modelame asi tutututuruturutu pose pose pose pose', CURRENT_TIMESTAMP, 1, 1);
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Perfume', 'Regalen perfume a sus compas hediondos', CURRENT_TIMESTAMP, 2, 2);
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Jijiji', 'NO LO SONIEEEEEEEEEEEEEEEEEEEEEEEEEEE EEIEIEIEI *alguien muere pisoteado*', CURRENT_TIMESTAMP, 3, 2);
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Motor psico', 'Junto a la hemoglobina me fui y ya no sangro mas. De la nada a la gloria me voy (asi me das mas...!) "Motor psico": el mercado de todo amor Lo que debes, camo puedes quedartelo.', CURRENT_TIMESTAMP, 3, 3);
INSERT INTO posts (title, description, postdate, neighborId, channelid)
VALUES ('Invocacion', 'Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo Lobo', CURRENT_TIMESTAMP, 3, 3);

-- Insert Comments
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('SI PERRO VAMOS LOS REDONDOS ES LO MEJOR QUE ME PSASO EN LA VIDA', CURRENT_TIMESTAMP, 1, 4);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('ESTO lo soñe despierto', CURRENT_TIMESTAMP, 2, 4);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('VAMOS EL INDIOOOOOOOOOOOOOOOo', CURRENT_TIMESTAMP, 1, 4);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('quien no comparte mi locura nunca entendera mi pasion', CURRENT_TIMESTAMP, 3, 4);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('This is a great post!', CURRENT_TIMESTAMP, 1, 1);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('Nice work on this one!', CURRENT_TIMESTAMP, 1, 2);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('I really like this post!', CURRENT_TIMESTAMP, 1, 3);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('Great content!', CURRENT_TIMESTAMP, 2, 1);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('Well done!', CURRENT_TIMESTAMP, 2, 2);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('I enjoyed reading this.', CURRENT_TIMESTAMP, 2, 3);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('Keep it up!', CURRENT_TIMESTAMP, 3, 1);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('Impressive!', CURRENT_TIMESTAMP, 3, 2);
INSERT INTO comments (comment, commentdate, neighborid, postid)
VALUES ('You re doing great!', CURRENT_TIMESTAMP, 3, 3);

-- Insert tags to a post
INSERT INTO posts_tags (postid, tagid) VALUES(1,1);
INSERT INTO posts_tags (postid, tagid) VALUES(1,2);
INSERT INTO posts_tags (postid, tagid) VALUES(1,3);
INSERT INTO posts_tags (postid, tagid) VALUES(2,1);
INSERT INTO posts_tags (postid, tagid) VALUES(2,3);
INSERT INTO posts_tags (postid, tagid) VALUES(3,2);



*/
