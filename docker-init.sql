-- This file is required because postgres does not accept hyphens from cli
CREATE DATABASE "paw-2023b-02";

CREATE USER "paw-2023b-02" WITH PASSWORD 'Totw34tOi';

GRANT ALL PRIVILEGES ON DATABASE "paw-2023b-02" TO "paw-2023b-02";