import { useEffect, useState } from 'react';
import styles from './PlaylistDetailModal.module.css';
import type { PlaylistInfo } from '../interfaces';
import { makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';

export interface TrackInfo {
  title: string;
  artist: string;
  durationSec: number;
}

export interface PlaylistDetailData {
  id: string;
  title: string;
  description: string;
  datetime: Date;
  tracks: TrackInfo[];
}

export interface PlaylistDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  playlistId: number | null;
}

export function PlaylistDetailModal({
  isOpen,
  onClose,
  playlistId
}: PlaylistDetailModalProps) {
  if (!isOpen || !playlistId) return null;
  
  const navigate = useNavigate();
  const [playlist, setPlaylist] = useState<PlaylistInfo | null>(null);

  const fetchPlaylistData = async () => {
    const playlistData : PlaylistInfo = await makeSafeAuthGet("/api/playlists/" + playlistId, navigate);
    setPlaylist(playlistData);
  }

  useEffect(() => {
    fetchPlaylistData();
  }, []);

  const formatDate = (isoDate: string): string => {
    return new Date(isoDate).toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: 'numeric',
      minute: 'numeric'
    });
  };

  const formatDuration = (sec: number) => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  return (
    (playlist) && (
    <div
      className={styles.overlay}
      onClick={onClose}
      aria-modal="true"
    >
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <header className={styles.header}>
          <h2 className={styles.title}>{playlist.name}</h2>
          <button 
            type="button" 
            onClick={onClose} 
            className={styles.closeBtn}
            aria-label="Закрыть окно"
          >
            ×
          </button>
        </header>

        <div className={styles.body}>
          <div className={styles.meta}>
            <span className={styles.metaLabel}>Дата и время:</span>
            <time className={styles.metaValue} dateTime={playlist.datetime}>{formatDate(playlist.datetime)}</time>
          </div>

          <p className={styles.description}>
            {playlist.description || 'Описание отсутствует'}
          </p>

          <h3 className={styles.tracksTitle}>
            Треки ({playlist.tracks.length})
          </h3>

          <div className={styles.trackList} role="list">
            {playlist.tracks.length === 0 ? (
              <p className={styles.emptyState}>В плейлисте пока нет треков</p>
            ) : (
              playlist.tracks.map((track, idx) => (
                <div key={`${playlist.id}-track-${idx}`} className={styles.trackRow} role="listitem">
                  <span className={styles.trackNum}>{String(idx + 1).padStart(2, '0')}</span>
                  <div className={styles.trackInfo}>
                    <span className={styles.trackName}>{track.title}</span>
                    <span className={styles.trackArtist}>{track.artistName}</span>
                  </div>
                  <span className={styles.trackDuration}>
                    {formatDuration(track.duration)}
                  </span>
                </div>
              ))
            )}
          </div>
        </div>

        <footer className={styles.footer}>
          <button type="button" onClick={onClose} className={styles.actionBtn}>
            Закрыть
          </button>
        </footer>
      </div>
    </div>
  ));
}