export interface ErrorResponse {
    status: string,
    message: string
}

export interface LoginResponse {
    token: string,
    privilege: string
}

export interface TrackDto {
  id: string;
  title: string;
  artistName: string;
  duration: number;
  genre: string;
}

export interface TrackScoreDto {
    track: TrackDto,
    score: number
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
    datetime: Date,
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