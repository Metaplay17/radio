CREATE TABLE artists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE audio_features_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE interaction_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);


CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash TEXT NOT NULL,
    email VARCHAR(255) NOT NULL,
    privilege_level SMALLINT NOT NULL
);

CREATE TABLE tracks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    genre_id INTEGER NOT NULL,
    artist_id INTEGER NOT NULL,
    duration INTEGER NOT NULL,
    CONSTRAINT fk_track_genre FOREIGN KEY (genre_id) REFERENCES genres(id),
    CONSTRAINT fk_track_artist FOREIGN KEY (artist_id) REFERENCES artists(id)
);

CREATE TABLE playlists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creator_id INT NOT NULL,
    description TEXT,
    datetime TIMESTAMP NOT NULL,
    duration INTEGER NOT NULL,
    CONSTRAINT fk_playlist_creator FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE licences (
    id BIGSERIAL PRIMARY KEY,
    track_id BIGINT NOT NULL,
    registered DATE NOT NULL,
    duration INTEGER NOT NULL,
    CONSTRAINT fk_licences_track FOREIGN KEY (track_id) REFERENCES tracks(id)
);


CREATE TABLE audio_feature (
    track_id BIGINT NOT NULL,
    feature_type_id INTEGER NOT NULL,
    value FLOAT NOT NULL,
    CONSTRAINT pk_audio_feature PRIMARY KEY (track_id, feature_type_id),
    CONSTRAINT fk_audio_feature_track FOREIGN KEY (track_id) REFERENCES tracks(id),
    CONSTRAINT fk_audio_feature_type FOREIGN KEY (feature_type_id) REFERENCES audio_features_types(id),
    CONSTRAINT chk_value_range CHECK (value BETWEEN 0 AND 1)
);

CREATE TABLE interaction (
    id BIGSERIAL PRIMARY KEY,
    track_id BIGINT NOT NULL,
    context VARCHAR(255),
    interaction_type_id INTEGER NOT NULL,
    datetime TIMESTAMP NOT NULL,
    CONSTRAINT fk_interaction_track FOREIGN KEY (track_id) REFERENCES tracks(id),
    CONSTRAINT fk_interaction_type FOREIGN KEY (interaction_type_id) REFERENCES interaction_types(id)
);


CREATE TABLE playlists_tracks (
    playlist_id BIGINT NOT NULL,
    track_id BIGINT NOT NULL,
    number SMALLINT NOT NULL,
    CONSTRAINT pk_playlists_tracks PRIMARY KEY (playlist_id, track_id),
    CONSTRAINT fk_pt_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id),
    CONSTRAINT fk_pt_track FOREIGN KEY (track_id) REFERENCES tracks(id)
);