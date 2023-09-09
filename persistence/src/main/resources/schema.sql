CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

/*



    DEJO ESTO COMENTADO PORQUE NO ESTOY SEGURO SI ESTE ARCHIVO ES EJECUTADO INSTANTANEAMENTE

------------------------------------------ CREATION -----------------------------------------------
-- The Posts and the Neighbors are the two main entities in this schema
-- A Neighbor belongs to a Specific Neighborhood, can write comments, can post, and can be subscribed to multiple Posts to receive follow-up information

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

-- Create Neighbors
create table if not exists neighbors(
    neighborId SERIAL PRIMARY KEY, -- Unique identifier for each neighbor
    mail VARCHAR(128) NOT NULL UNIQUE, -- Email address of the neighbor (must be unique)
    name VARCHAR(64) NOT NULL, -- First name of the neighbor
    surname VARCHAR(64) NOT NULL, -- Last name of the neighbor
    -- profilePicture BYTEA, -- Only image for the profile, nullable
    neighborhoodId INT NOT NULL, -- ID of the neighborhood to which the neighbor belongs
    foreign key (neighborhoodId) references neighborhoods(neighborhoodId) ON DELETE CASCADE -- Reference to the neighborhoods table
);

-- Create Post
create table if not exists posts(
    postId SERIAL PRIMARY KEY, -- Unique identifier for each post
    title VARCHAR(128) NOT NULL, -- Title of the post
    description TEXT NOT NULL, -- Description of the post
    -- postPicture BYTEA, -- Only image for the post, nullable
    postDate DATE NOT NULL, -- Date when the post was created
    neighborId INT NOT NULL, -- ID of the neighbor who created the post
    foreign key (neighborId) references neighbors(neighborId) ON DELETE CASCADE -- Reference to the neighbors table
);

-- Create Comments
create table if not exists comments(
    commentId SERIAL NOT NULL PRIMARY KEY, -- Unique identifier for each comment
    comment VARCHAR(512) NOT NULL, -- The comment text
    commentDate DATE NOT NULL, -- Date when the comment was posted

    neighborId INT NOT NULL, -- ID of the neighbor who posted the comment
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

/*

 */





*/
