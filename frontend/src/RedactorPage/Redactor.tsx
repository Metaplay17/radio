import { useNavigate } from "react-router-dom";
import React, { useState, useCallback } from 'react';
import styles from './Redactor.module.css';
import type { OkResponse, PlaylistForm, TrackScoreDto } from "../interfaces";
import { ErrorResponseException, logout, makeSafeAuthPost } from "../utils";
import { InfoModal } from "../InfoModal/InfoModal";

export function RedactorPage() {
  const navigate = useNavigate();

  if (localStorage.getItem('privilege') !== 'ROLE_REDACTOR') {
    window.location.href = '/login';
  }
  const username = localStorage.getItem("username");

  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  // Левая панель
  const [filters, setFilters] = useState({
    duration: '',
    timeOfDay: 'day',
    date: '',
    anchorTrack: ''
  });

  // Центральная панель
  const [playlist, setPlaylist] = useState({
    name: '',
    description: '',
    dateTime: ''
  });

  // Выбранные треки
  const [tracks, setTracks] = useState<TrackScoreDto[]>([]);

  const handleFilterChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  }, []);

  const handleGenerate = async () => {
    try {
        const json : PlaylistForm = await makeSafeAuthPost("/api/playlists/form", navigate, {
            duration: Number(filters.duration),
            daytime: filters.timeOfDay.toUpperCase(),
            date: filters.date,
            weekday: new Date(filters.date).getDay() + 1,
            anchorTrack: filters.anchorTrack == "" ? null : filters.anchorTrack
        });
        setTracks([...(json.tracks || [])]);
    }
    catch (error : any) {
        if (error instanceof ErrorResponseException) {
            setModalMessage(error.message);
            setIsModalOpen(true);
        }
        else {
            console.error(error);
        }
    }
  };

  const handleConfirm = async () => {
    try {
        const json : OkResponse = await makeSafeAuthPost("/api/playlists", navigate, {
            name: playlist.name,
            datetime: playlist.dateTime,
            description: playlist.description,
            tracks: tracks.map(t => {
                return {
                    title: t.track.title,
                    artistName: t.track.artistName,
                    duration: t.track.duration
                } 
            })
        });
        setModalMessage(json.message);
        setIsModalOpen(true);
    }
    catch (error : any) {
        if (error instanceof ErrorResponseException) {
            setModalMessage(error.message);
            setIsModalOpen(true);
        }
        else {
            console.error(error);
        }
    }
  };

  const handlePlaylistChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setPlaylist(prev => ({ ...prev, [name]: value }));
  }, []);

  const handleAddTrack = useCallback(() => {
    const newTrack: TrackScoreDto = {
      track: {
        id: crypto.randomUUID(),
        title: `Трек ${tracks.length + 1}`,
        artistName: 'Исполнитель 1',
        duration: Math.floor(Math.random() * 180) + 150,
        genreName: 'Жанр 1'
      },
      score: 100
    };
    setTracks(prev => [...prev, newTrack]);
  }, [tracks.length]);

  const handleDeleteTrack = useCallback((id: string) => {
    setTracks(prev => prev.filter(t => t.track.id !== id));
  }, []);

  const handleLogout = useCallback(() => {
    logout(navigate);
  }, []);

  const formatDuration = (sec: number): string => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className={styles.page}>
      <InfoModal isOpen={isModalOpen} message={modalMessage} title="Информация" onClose={() => {setIsModalOpen(false)}}  />
      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>Страница музыкального редактора</h1>
        <div className={styles.userBlock}>
          <span className={styles.username}>{username}</span>
          <button onClick={handleLogout} className={styles.logoutBtn}>Выход</button>
        </div>
      </header>

      {/* Навигация */}
      <nav className={styles.navButtons}>
        <button className={styles.navBtn} onClick={() => navigate("/redactor/playlist-history")}>История плейлистов</button>
        <button className={styles.navBtn} onClick={() => navigate("/tracks")}>Обзор треков</button>
      </nav>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Левая панель: Параметры */}
        <section className={styles.leftPanel}>
          <h2 className={styles.panelTitle}>Параметры генерации</h2>
          <form className={styles.filterForm} onSubmit={(e) => e.preventDefault()}>
            <div className={styles.formGroup}>
              <label htmlFor="duration">Длительность (сек)</label>
              <input
                id="duration"
                name="duration"
                type="number"
                min="0"
                step="1"
                value={filters.duration}
                onChange={handleFilterChange}
                placeholder="Например: 300"
                className={styles.input}
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="timeOfDay">Время суток</label>
              <select id="timeOfDay" name="timeOfDay" value={filters.timeOfDay} onChange={handleFilterChange} className={styles.select}>
                <option value="morning">Утро</option>
                <option value="day">День</option>
                <option value="evening">Вечер</option>
                <option value="night">Ночь</option>
              </select>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="date">Дата</label>
              <input id="date" name="date" type="date" value={filters.date} onChange={handleFilterChange} className={styles.input} />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="customSelector">Якорный трек</label>
              <input
                id="customSelector"
                name="anchorTrack"
                list="tracksOptions"
                value={filters.anchorTrack}
                onChange={handleFilterChange}
                placeholder="Начните вводить..."
                className={styles.input}
              />
              <datalist id="tracksOptions">
                <option value="XDD" />
              </datalist>
            </div>

            <button className={styles.primaryBtn} onClick={handleGenerate}>Сформировать</button>
          </form>
        </section>

        {/* Правая панель: Редактор плейлиста */}
        <section className={styles.rightPanel}>
          <div className={styles.playlistForm}>
            <div className={styles.formGroup}>
              <label htmlFor="plName">Название плейлиста</label>
              <input id="plName" name="name" value={playlist.name} onChange={handlePlaylistChange} placeholder="Вечерний чилл" className={styles.input} />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="plDesc">Описание</label>
              <textarea id="plDesc" name="description" value={playlist.description} onChange={handlePlaylistChange} placeholder="Опишите настроение или концепцию..." className={styles.textarea} />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="plDate">Дата и время публикации</label>
              <input id="plDate" name="dateTime" type="datetime-local" value={playlist.dateTime} onChange={handlePlaylistChange} className={styles.input} />
            </div>
          </div>

          <div className={styles.tracksSection}>
            <button onClick={handleAddTrack} className={styles.secondaryBtn}>+ Добавить трек</button>
            <div className={styles.tracksList}>
                <div className={styles.trackInfo}>
                    <span className={styles.trackTitle}>Название</span>
                    <span className={styles.trackArtist}>Исполнитель</span>
                    <span className={styles.trackDuration}>Длительность</span>
                    <span className={styles.trackTitle}>Score</span>
                </div>
              {tracks.length === 0 ? (
                <p className={styles.emptyState}>Список треков пуст. Добавьте первый трек, чтобы начать.</p>
              ) : (
                tracks.map(track => (
                  <div key={track.track.id} className={styles.trackRow}>
                    <div className={styles.trackInfo}>
                      <span className={styles.trackTitle}>{track.track.title}</span>
                      <span className={styles.trackArtist}>{track.track.artistName}</span>
                      <span className={styles.trackDuration}>{formatDuration(track.track.duration)}</span>
                      <span className={styles.trackTitle}>{track.score}</span>
                    </div>
                    <button 
                      onClick={() => handleDeleteTrack(track.track.id)} 
                      className={styles.deleteBtn} 
                      aria-label={`Удалить трек ${track.track.title}`}
                    >
                      Удалить
                    </button>
                  </div>
                ))
              )}
              <button className={styles.confirmPlaylistButton} onClick={handleConfirm}>Утвердить</button>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}