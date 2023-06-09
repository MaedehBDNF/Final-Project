CREATE TABLE IF NOT EXISTS "file" (
    id              SERIAL4 PRIMARY KEY,
    name            VARCHAR UNIQUE NOT NULL,
    "memeType"      VARCHAR(6) NOT NULL,
    path            VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "user" (
    id                  SERIAL4 PRIMARY KEY,
    username            VARCHAR(26) UNIQUE NOT NULL,
    password            VARCHAR NOT NULL,
    email               VARCHAR NOT NULL,
    "profilePictureId"  INT REFERENCES "file" (id)
);

CREATE TABLE IF NOT EXISTS "friend" (
    "userId"    INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    "friendId"  INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS "genre" (
    id          SERIAL4 PRIMARY KEY,
    name        VARCHAR(26) NOT NULL,
    description VARCHAR(256) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "artist" (
    id              SERIAL4 PRIMARY KEY,
    name            VARCHAR(256) DEFAULT NULL,
    genre           INT REFERENCES "genre" (id),
    biography       VARCHAR(512) DEFAULT NULL,
    "profilePictureId"  INT REFERENCES "file" (id),
    "socialLinks"   VARCHAR ARRAY
);

CREATE TABLE IF NOT EXISTS "following" (
    "userId"    INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    "artistId"  INT REFERENCES "artist" (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS "album" (
    id              SERIAL4 PRIMARY KEY,
    title           VARCHAR NOT NULL,
    "artistId"      INT REFERENCES "artist" (id),
    "genreId"       INT REFERENCES "genre" (id),
    "releaseDate"   DATE DEFAULT NULL,
    popularity      INT DEFAULT 0,
    "coverId"       INT REFERENCES "file" (id)
);

CREATE TABLE IF NOT EXISTS "music" (
    id              SERIAL4 PRIMARY KEY,
    title           VARCHAR NOT NULL,
    "artistId"      INT REFERENCES "artist" (id) DEFAULT NULL,
    "genreId"       INT REFERENCES "genre" (id) DEFAULT Null,
    "albumId"       INT REFERENCES "album" (id) NOT NULL,
    duration        INT CHECK ( duration > 0 ),
    "releaseDate"   DATE DEFAULT NULL,
    popularity      INT DEFAULT 0,
    lyric           VARCHAR DEFAULT NULL,
    "coverId"       INT REFERENCES "file" (id),
    "trackFile"     INT REFERENCES "file" (id)
);

CREATE TABLE IF NOT EXISTS "playlist" (
    id              SERIAL4 PRIMARY KEY,
    title           VARCHAR NOT NULL,
    "creatorId"     INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    description     VARCHAR DEFAULT NULL,
    popularity      INT DEFAULT 0,
    "isPrivate"     BOOLEAN DEFAULT FALSE,
    "coverId"       INT REFERENCES "file" (id)
);

CREATE TABLE IF NOT EXISTS "playlistTrack" (
    "playlistId"    INT REFERENCES "playlist" (id) ON DELETE CASCADE NOT NULL,
    "musicId"       INT REFERENCES "music" (id) ON DELETE CASCADE NOT NULL,
    turn            DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS "musicComments" (
    "musicId"   INT REFERENCES "music" (id) ON DELETE CASCADE NOT NULL,
    comment     VARCHAR
);

CREATE TABLE IF NOT EXISTS "userPlaylists" (
    "userId"        INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    "playlistId"    INT REFERENCES "playlist" (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS "userAlbums" (
    "userId"    INT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    "albumId"   INT REFERENCES "album" (id) ON DELETE CASCADE NOT NULL
);