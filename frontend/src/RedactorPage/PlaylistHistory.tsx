import React, { useState, useCallback, useEffect } from 'react';
import styles from './PlaylistHistory.module.css';
import type { PlaylistDto } from '../interfaces';
import { logout, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { PlaylistDetailModal } from './PlaylistDetailModal';

export function PlaylistHistoryPage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  const [isOpenModal, setIsOpenModal] = useState(false);
  const [selectedPlaylistId, setSelectedPlaylistId] = useState<number | null>(null);

  if (localStorage.getItem('privilege') !== 'ROLE_REDACTOR') {
    navigate('/login');
  }

  // Состояние фильтра и пагинации
  const [filterDate, setFilterDate] = useState('');
  const [lastDatetime, setLastDatetime] = useState<Date>(new Date(Date.now()));
  const [playlists, setPlaylists] = useState<PlaylistDto[]>([]);
  const [nextPlaylists, setNextPlaylists] = useState<PlaylistDto[]>([]);
  const [prevPlaylists, setPrevPlaylists] = useState<PlaylistDto[]>([]);

  const fetchPlaylists = async () => {
    if (lastDatetime.getDate() != new Date(Date.now()).getDate()) {
      const playlistsPrev : PlaylistDto[] = await makeSafeAuthGet(`/api/playlists?lastDatetime=${playlists[0].datetime.getMilliseconds()}&date=${filterDate}&isNext=false`, navigate);
      setPrevPlaylists(playlistsPrev);
    }
    const playlistsCurrent : PlaylistDto[] = await makeSafeAuthGet(`/api/playlists?lastDatetime=${lastDatetime.getMilliseconds()}&date=${filterDate}&isNext=true`, navigate);
    setSelectedPlaylistId(playlistsCurrent[0].id);
    setPlaylists(playlistsCurrent);
    setLastDatetime(playlistsCurrent[playlistsCurrent.length - 1].datetime);
    const playlistsNext : PlaylistDto[] = await makeSafeAuthGet(`/api/playlists?lastDatetime=${playlistsCurrent[playlistsCurrent.length - 1].datetime.getMilliseconds()}&date=${filterDate}&isNext=true`, navigate);
    setNextPlaylists(playlistsNext);
  }

  useEffect(() => {
    fetchPlaylists();
  }, []);

useEffect(() => {
    fetchPlaylists();
  }, [lastDatetime]);

  // Обработчики
  const handleFilterChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterDate(e.target.value);
  }, []);

  const handlePageNext = () => {
    setLastDatetime(playlists[playlists.length - 1].datetime);
  };

  const handlePagePrev = () => {
    setLastDatetime(playlists[0].datetime);
  };

  const handlePlaylistClick = useCallback((id: number) => {
    setSelectedPlaylistId(id);
    setIsOpenModal(true);
  }, []);

  // Форматирование длительности
  const formatDuration = (sec: number): string => {
    const h = Math.floor(sec / 3600);
    const m = Math.floor((sec % 3600) / 60);
    return h > 0 ? `${h} ч ${m} мин` : `${m} мин`;
  };

  // Форматирование даты для отображения
  const formatDate = (isoDate: Date): string => {
    return new Date(isoDate).toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  };

  return (
    <div className={styles.page}>
        <PlaylistDetailModal isOpen={isOpenModal} onClose={() => {setIsOpenModal(false)}} playlistId={selectedPlaylistId} />
      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>Страница музыкального редактора</h1>
        <div className={styles.userBlock}>
          <span className={styles.username}>{username}</span>
          <button onClick={() => logout(navigate)} className={styles.logoutBtn}>Выход</button>
        </div>
      </header>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Фильтр */}
        <section className={styles.filterBar}>
          <div className={styles.filterGroup}>
            <label htmlFor="history-date" className={styles.filterLabel}>
              Фильтр по дате
            </label>
            <input
              id="history-date"
              type="date"
              value={filterDate}
              onChange={handleFilterChange}
              className={styles.dateInput}
            />
          </div>
          <button onClick={fetchPlaylists} className={styles.searchBtn}>
            Найти
          </button>
          <button onClick={() => navigate("/redactor")} className={styles.backBtn}>
            Назад
          </button>
        </section>

        {/* Список плейлистов */}
        <section className={styles.listSection}>
          {playlists.length === 0 ? (
            <div className={styles.emptyState}>
              <p>Плейлисты не найдены</p>
              {filterDate && (
                <button onClick={() => setFilterDate('')} className={styles.clearFilterBtn}>
                  Сбросить фильтр
                </button>
              )}
            </div>
          ) : (
            <div className={styles.playlistGrid}>
              {playlists.map(playlist => (
                <button
                  key={playlist.id}
                  type="button"
                  className={styles.playlistItem}
                  onClick={() => handlePlaylistClick(playlist.id)}
                  aria-label={`Открыть плейлист ${playlist.name}`}
                >
                  <div className={styles.itemInfo}>
                    <span className={styles.itemTitle}>{playlist.name}</span>
                    <time className={styles.itemDate}>{formatDate(playlist.datetime)}</time>
                  </div>
                  <span className={styles.itemDuration}>⏱ {formatDuration(playlist.duration)}</span>
                </button>
              ))}
            </div>
          )}

          {/* Пагинация */}
          {(
            <nav className={styles.pagination} aria-label="Навигация по страницам">
              <button
                onClick={() => handlePagePrev()}
                disabled={prevPlaylists.length == 0}
                className={styles.pageBtn}
                aria-label="Предыдущая страница"
              >
                ←
              </button>

              <button
                onClick={() => handlePageNext()}
                disabled={nextPlaylists.length === 0}
                className={styles.pageBtn}
                aria-label="Следующая страница"
              >
                →
              </button>
            </nav>
          )}
        </section>
      </main>
    </div>
  );
}