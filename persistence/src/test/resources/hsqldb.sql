SET DATABASE SQL SYNTAX PGS TRUE;

create table if not exists neighborhoods(
    neighborhoodId INTEGER IDENTITY NOT NULL PRIMARY KEY, -- Unique identifier for each neighborhood
    neighborhoodName VARCHAR(128) -- Name of the neighborhood
);

insert into neighborhoods (neighborhoodName) values ('Test Neighborhood')