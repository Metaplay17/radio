export interface ErrorResponse {
    status: string,
    message: string
}

export interface LoginResponse {
    token: string,
    privilege: string
}

export interface TrackDto {
  id: number;
  title: string;
  artistName: string;
  duration: number;
  genreName: string;
}

export interface TrackScoreDto {
    track: TrackDto,
    score: number | null
}

export interface PlaylistForm {
    tracks: TrackScoreDto[],
}

export interface OkResponse {
    message: string
}

export interface PlaylistDto {
    id: number,
    name: string,
    description: string,
    datetime: string,
    duration: number
}

export interface PlaylistInfo {
    id: number,
    name: string,
    description: string,
    datetime: string,
    duration: number,
    tracks: TrackDto[]
}

export interface ArtistDto {
    id: number,
    name: string
}

export interface GenreDto {
    id: number,
    name: string
}

export interface LicenseDto {
    id: number,
    track: TrackDto,
    registered: string,
    duration: number
}

export interface FeatureTypeDto {
    id: number,
    name: string
}

export interface FeatureDto {
    track: TrackDto,
    featureTypeId: number,
    name: string,
    value: number
}