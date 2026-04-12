import React, { useState, useEffect } from 'react';
import styles from './TrackPage.module.css';
import type { ArtistDto, GenreDto, TrackDto } from '../interfaces';
import { ErrorResponseException, logout, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

const ITEMS_PER_PAGE = 10;

export function TrackDatabasePage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  // Проверка прав доступа
  if (!['ROLE_REDACTOR', 'ROLE_CONTENT_MANAGER', 'ROLE_ANALYST'].includes(localStorage.getItem('privilege') || '')) {
    window.location.href = '/login';
    return null;
  }

  // Состояние фильтров
  const [searchQuery, setSearchQuery] = useState('');
  const [genreFilter, setGenreFilter] = useState('');
  const [artistFilter, setArtistFilter] = useState('');
  const [activeLicenseOnly, setActiveLicenseOnly] = useState(false);

  const [lastId, setLastId] = useState(0);
  const [tracks, setTracks] = useState<TrackDto[]>([]);
  const [nextTracks, setNextTracks] = useState<TrackDto[]>([]);
  
  const [artists, setArtists] = useState<ArtistDto[]>([]);
  const [genres, setGenres] = useState<GenreDto[]>([]);

  const [isLoading, setIsLoading] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  const fetchTracks = async (lastId : number, activeLicenseOnly : boolean, genreFilter : string, searchQuery : string, artistFilter : string) => {
    setIsLoading(true);
    try {
      const current = await makeSafeAuthGet(
        `/api/tracks?lastId=${lastId}&isLicensedOnly=${activeLicenseOnly}&genre=${genreFilter}&titlePattern=${searchQuery}&artistName=${artistFilter}`,
        navigate
      );
      setTracks(current);

      const next = await makeSafeAuthGet(
        `/api/tracks?lastId=${lastId + 10}&isLicensedOnly=${activeLicenseOnly}&genre=${genreFilter}&titlePattern=${searchQuery}&artistName=${artistFilter}`,
        navigate
      );
      setNextTracks(next);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
      console.error('Ошибка загрузки треков:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchGenres = async () => {
    setIsLoading(true);
    try {
      const fetchedGenres : GenreDto[] = await makeSafeAuthGet(
        `/api/genres`,
        navigate
      );
      setGenres(fetchedGenres);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
    } finally {
      setIsLoading(false);
    }
  }

  const fetchArtists = async (namePattern : string) => {
    setIsLoading(true);
    try {
      const fetchedArtists : ArtistDto[] = await makeSafeAuthGet(
        `/api/artists?namePattern=${namePattern}&lastId=${0}`,
        navigate
      );
      setArtists(fetchedArtists);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    fetchTracks(lastId, activeLicenseOnly, genreFilter, searchQuery, artistFilter);
    fetchGenres();
  }, []);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  const handleGenreChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGenreFilter(e.target.value);
  };

  const handleArtistSubmit = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      fetchArtists(artistFilter);
    }
  };

  const handleLicenseChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setActiveLicenseOnly(e.target.checked);
    fetchTracks(lastId, e.target.checked, genreFilter, searchQuery, artistFilter);
  };

  // Пагинация
  const handlePageNext = () => {
    setLastId(prev => prev + ITEMS_PER_PAGE);
  };

  const handlePagePrev = () => {
    setLastId(prev => Math.max(0, prev - ITEMS_PER_PAGE));
  };

  // Форматирование длительности
  const formatDuration = (sec: number): string => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className={styles.page}>
        <InfoModal title={"Информация"} isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} message={modalMessage} />
      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>База треков</h1>
        <div className={styles.userBlock}>
          <span className={styles.username}>{username}</span>
          <button onClick={() => logout(navigate)} className={styles.logoutBtn}>Выход</button>
        </div>
      </header>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Панель фильтров */}
        <section className={styles.filterBar}>
          <div className={styles.filterGroup}>
            <label htmlFor="track-search" className={styles.filterLabel}>Поиск по названию</label>
            <input
              id="track-search"
              type="text"
              value={searchQuery}
              onChange={handleSearchChange}
              placeholder="Например: Midnight..."
              className={styles.input}
            />
          </div>

          <div className={styles.filterGroup}>
            <label htmlFor="track-genre" className={styles.filterLabel}>Жанр</label>
            <input
              id="track-genre"
              list="genre-options"
              value={genreFilter}
              onChange={handleGenreChange}
              placeholder="Начните вводить..."
              className={styles.input}
            />
            <datalist id="genre-options">
              {
                genres.map(genre => {
                  if (genre.name.includes(genreFilter) || genreFilter == '') {
                    return <option value={genre.name} />
                  }
                })
              }
            </datalist>
          </div>

        <div className={styles.filterGroup}>
            <label htmlFor="track-artist" className={styles.filterLabel}>Исполнитель</label>
            <input
              id="track-artist"
              list="artist-options"
              value={artistFilter}
              onChange={(e) => setArtistFilter(e.target.value)}
              onKeyDown={handleArtistSubmit}
              placeholder="Начните вводить..."
              className={styles.input}
            />
            <datalist id="artist-options">
              {
                artists.map(artist => <option value={artist.name} />)
              }
            </datalist>
          </div>

          <label className={styles.checkboxLabel}>
            <input
              type="checkbox"
              checked={activeLicenseOnly}
              onChange={handleLicenseChange}
              className={styles.checkbox}
            />
            <span>Только с активной лицензией</span>
          </label>

          <button onClick={() => fetchTracks(lastId, activeLicenseOnly, genreFilter, searchQuery, artistFilter)} className={styles.searchBtn} disabled={isLoading}>
            {isLoading ? 'Загрузка...' : 'Найти'}
          </button>

          <button onClick={() => history.back()} className={styles.backBtn}>
            Назад
          </button>
        </section>

        {/* Список треков */}
        <section className={styles.listSection}>
          {tracks.length === 0 && !isLoading ? (
            <div className={styles.emptyState}>
              <p>Треки не найдены</p>
            </div>
          ) : (
            <div className={styles.trackGrid}>
            {(
                tracks.map(track => (
                  <div
                    key={track.id}
                    className={styles.trackItem}
                    role="listitem"
                  >
                    <div className={styles.trackInfo}>
                      <span className={styles.trackTitle}>{track.title}</span>
                      <span className={styles.trackArtist}>{track.artistName}</span>
                    </div>
                    <div className={styles.trackMeta}>
                      <span className={styles.trackGenre}>{track.genreName}</span>
                      <span className={styles.trackDuration}>{formatDuration(track.duration)}</span>
                    </div>
                    {activeLicenseOnly ? (
                      <span className={styles.licenseBadge}>✓ Лицензия</span>
                    ) : (
                       <span></span>
                    )}
                  </div>
                ))
              )}
            </div>
          )}

        <button onClick={() => {
                setSearchQuery('');
                setGenreFilter('');
                setArtistFilter('');
                setActiveLicenseOnly(false);
                fetchTracks(0, false, '', '', '');
            }} className={styles.clearFilterBtn}>
            Сбросить фильтры
        </button>

          {/* Пагинация */}
          {tracks.length > 0 && (
            <nav className={styles.pagination} aria-label="Навигация по страницам">
              <button
                onClick={handlePagePrev}
                disabled={lastId === 0 || isLoading}
                className={styles.pageBtn}
                aria-label="Предыдущая страница"
              >
                ← Назад
              </button>

              <span className={styles.pageInfo}>
                Страница {Math.floor(lastId / ITEMS_PER_PAGE) + 1}
              </span>

              <button
                onClick={handlePageNext}
                disabled={nextTracks.length === 0 || isLoading}
                className={styles.pageBtn}
                aria-label="Следующая страница"
              >
                Вперёд →
              </button>
            </nav>
          )}
        </section>
      </main>
    </div>
  );
}