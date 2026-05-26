import { useState, useEffect, type KeyboardEvent } from 'react';
import styles from './ContentManager.module.css';
import { ErrorResponseException, logout, makeSafeAuthGet, makeSafeAuthPost } from '../utils';
import { useNavigate } from 'react-router-dom';
import type { ArtistDto, GenreDto, OkResponse } from '../interfaces';
import { InfoModal } from '../InfoModal/InfoModal';

export function ContentManagerPage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  // Проверка прав доступа
  if (localStorage.getItem('privilege') !== 'ROLE_CONTENT_MANAGER') {
    window.location.href = "/login";
    return null;
  }

  // Состояния форм
  const [artistName, setArtistName] = useState('');
  const [trackName, setTrackName] = useState('');
  const [genreName, setGenreName] = useState('');

  const [trackArtistQuery, setTrackArtistQuery] = useState('');
  const [trackGenreQuery, setTrackGenreQuery] = useState('');
  const [trackDuration, setTrackDuration] = useState<number | null>(null);

  // Данные для селекторов
  const [artists, setArtists] = useState<ArtistDto[]>([]);
  const [genres, setGenres] = useState<GenreDto[]>([]);

  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  const fetchGenres = async () => {
    try {
      const genres : GenreDto[] = await makeSafeAuthGet("/api/genres", navigate)
      setGenres(genres);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
      console.error('Ошибка загрузки справочников:', err);
    }
  }

  useEffect(() => {
    fetchGenres();
  }, []);

  const handleAddArtist = async () => {
    try {
        const json : OkResponse = await makeSafeAuthPost("/api/artists", navigate, {
            name: artistName
        });
        setArtistName('');
        setModalMessage(json.message);
        setIsModalOpen(true);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
        console.error(err);
    }
  }

    const handleAddTrack = async () => {
    try {
        const json : OkResponse = await makeSafeAuthPost("/api/tracks", navigate, {
            title: trackName,
            artist: trackArtistQuery,
            genre: trackGenreQuery,
            duration: trackDuration
        });
        setModalMessage(json.message);
        setIsModalOpen(true);
        setTrackArtistQuery('');
        setTrackGenreQuery('');
        setTrackName('');
        setTrackDuration(0);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
        console.error(err);
    }
  }

    const handleAddGenre = async () => {
    try {
        const json : OkResponse = await makeSafeAuthPost("/api/genres", navigate, {
            name: genreName
        });
        setModalMessage(json.message);
        setIsModalOpen(true);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
        console.error(err);
    }
  }

  const searchArtists = async (e : KeyboardEvent<HTMLInputElement>) => {
    if (e.key == "Enter") {
        try {
            const json : ArtistDto[] = await makeSafeAuthGet(`/api/artists?namePattern=${trackArtistQuery}&lastId=${0}`, navigate);
            setArtists(json);
        } catch (err) {
            if (err instanceof ErrorResponseException) {
                setModalMessage(err.message);
                setIsModalOpen(true);
            }
            console.error(err);
        }
    }
  }

  return (
    <div className={styles.page}>
      <InfoModal isOpen={isModalOpen} message={modalMessage} title="Информация" onClose={() => {setIsModalOpen(false)}}  />
      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>Страница контент-менеджера</h1>
        <div className={styles.userBlock}>
          <span className={styles.username}>{username}</span>
          <button onClick={() => logout(navigate)} className={styles.logoutBtn}>Выход</button>
        </div>
      </header>

      {/* Навигация */}
      <nav className={styles.navButtons}>
        <button onClick={() => navigate('/tracks')} className={styles.navBtn}>
          Обзор треков
        </button>
        <button onClick={() => navigate('/genres')} className={styles.navBtn}>
          Обзор жанров
        </button>
        <button onClick={() => navigate('/artists')} className={styles.navBtn}>
          Обзор исполнителей
        </button>
      </nav>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Блок: Добавить исполнителя */}
        <section className={styles.card}>
          <h2 className={styles.cardTitle}>Добавить исполнителя</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="artist-name" className={styles.label}>Название исполнителя</label>
              <input
                id="artist-name"
                type="text"
                value={artistName}
                onChange={(e) => setArtistName(e.target.value)}
                placeholder="Например: The Beatles"
                className={styles.input}
                required
              />
            </div>
            <button 
              onClick={handleAddArtist}
              className={styles.primaryBtn}
              disabled={!artistName.trim()}
            >
              {'Добавить'}
            </button>
          </div>
        </section>

        {/* Блок: Добавить трек */}
        <section className={styles.card}>
          <h2 className={styles.cardTitle}>Добавить трек</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="track-name" className={styles.label}>Название трека</label>
              <input
                id="track-name"
                type="text"
                value={trackName}
                onChange={(e) => setTrackName(e.target.value)}
                placeholder="Например: Yesterday"
                className={styles.input}
                required
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="track-artist" className={styles.label}>Исполнитель</label>
              <input
                id="track-artist"
                list="artist-options"
                value={trackArtistQuery}
                onChange={(e) => setTrackArtistQuery(e.target.value)}
                onKeyDown={(e) => searchArtists(e)}
                placeholder="Начните вводить..."
                className={styles.input}
                required
              />
              <datalist id="artist-options">
                {artists.map(artist => (
                  <option key={artist.id} value={artist.name} />
                ))}
              </datalist>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="track-genre" className={styles.label}>Жанр</label>
              <input
                id="track-genre"
                list="genre-options"
                value={trackGenreQuery}
                onChange={(e) => setTrackGenreQuery(e.target.value)}
                placeholder="Начните вводить..."
                className={styles.input}
                required
              />
              <datalist id="genre-options">
                {genres.map(genre => (
                  <option key={genre.id} value={genre.name} />
                ))}
              </datalist>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="track-duration" className={styles.label}>Длительность (сек)</label>
              <input
                id="track-duration"
                type="number"
                value={trackDuration || 0}
                min="0"
                max="600"
                onChange={(e) => setTrackDuration(Number(e.target.value))}
                className={styles.input}
                required
              />
            </div>

            <button 
              onClick={handleAddTrack}
              className={styles.primaryBtn}
              disabled={!trackName.trim() || !trackArtistQuery.trim() || !trackGenreQuery.trim()}
            >
              {'Добавить'}
            </button>
          </div>
        </section>

        {/* Блок: Добавить жанр */}
        <section className={styles.card}>
          <h2 className={styles.cardTitle}>Добавить жанр</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="genre-name" className={styles.label}>Название жанра</label>
              <input
                id="genre-name"
                type="text"
                value={genreName}
                onChange={(e) => setGenreName(e.target.value)}
                placeholder="Например: Lo-Fi"
                className={styles.input}
                required
              />
            </div>
            <button 
              className={styles.primaryBtn}
              disabled={!genreName.trim()}
              onClick={handleAddGenre}
            >
              {'Добавить'}
            </button>
          </div>
        </section>
      </main>
    </div>
  );
}