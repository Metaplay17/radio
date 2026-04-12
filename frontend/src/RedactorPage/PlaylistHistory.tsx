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

  const [filterDate, setFilterDate] = useState('');
  const [playlists, setPlaylists] = useState<PlaylistDto[]>([]);
  const [nextPlaylists, setNextPlaylists] = useState<PlaylistDto[]>([]);
  const [prevPlaylists, setPrevPlaylists] = useState<PlaylistDto[]>([]);

  // Добавляем отдельное состояние для "якоря" пагинации
  const [pageAnchor, setPageAnchor] = useState<Date>(new Date(Date.now()));

  const fetchPlaylists = async (
    filterDate: string, 
    anchorDate: Date, 
    direction: 'current' | 'prev' | 'next' = 'current'
  ) => {
    try {
      // ✅ Используем getTime() вместо getMilliseconds()
      const timestamp = anchorDate.getTime();
      const isNext = direction !== 'prev';
      
      const url = `/api/playlists?lastDatetime=${timestamp}&date=${filterDate}&isNext=${isNext}`;
      const result: PlaylistDto[] = await makeSafeAuthGet(url, navigate);
      
      if (direction === 'current') {
        setPlaylists(result);
        
        if (result.length > 0) {
          setSelectedPlaylistId(result[0].id);
          // ✅ Обновляем "якорь" для следующей пагинации
          setPageAnchor(new Date(result[result.length - 1].datetime));
        } else {
          setSelectedPlaylistId(null);
        }
      } else if (direction === 'prev') {
        setPrevPlaylists(result);
      } else {
        setNextPlaylists(result);
      }
    } catch (error) {
      console.error(`Ошибка при загрузке плейлистов (${direction}):`, error);
      // Опционально: показать ошибку пользователю
    }
  };

  // 1. Загружаем основной список при изменении фильтра ИЛИ якоря пагинации
  useEffect(() => {
    fetchPlaylists(filterDate, pageAnchor, 'current');
  }, [filterDate, pageAnchor]);

  // 2. Предзагружаем соседние страницы ПОСЛЕ того, как основной список загрузился
  useEffect(() => {
    if (playlists.length === 0) return;
    
    const firstTime = new Date(playlists[0].datetime);
    const lastTime = new Date(playlists[playlists.length - 1].datetime);
    
    // Загружаем предыдущую и следующую страницы параллельно
    fetchPlaylists(filterDate, firstTime, 'prev');
    fetchPlaylists(filterDate, lastTime, 'next');
  }, [playlists, filterDate]); // ✅ playlists в зависимостях — триггер после загрузки

  const handleFilterChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = e.target.value;
    setFilterDate(newDate);
    // ✅ При смене фильтра сбрасываем якорь на "сейчас" → первая страница
    setPageAnchor(new Date(Date.now()));
  }, []);

  // Переход на следующую страницу: якорь = последний элемент текущей страницы
  const handlePageNext = () => {
    if (playlists.length > 0) {
      setPageAnchor(new Date(playlists[playlists.length - 1].datetime));
    }
  };

  // Переход на предыдущую страницу: 
  // ⚠️ Здесь нужна логика "якоря" для предыдущей страницы. 
  // Простой вариант: использовать первый элемент текущей страницы как точку отсчёта
  const handlePagePrev = () => {
    if (prevPlaylists.length > 0) {
      // Загружаем prevPlaylists как основную страницу
      setPlaylists(prevPlaylists);
      setPageAnchor(new Date(prevPlaylists[0].datetime));
      setPrevPlaylists([]); // Очищаем, чтобы не дублировать
    }
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
          <button onClick={() => {
            setPageAnchor(new Date(Date.now()));
          }} className={styles.searchBtn}>
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
                    <time className={styles.itemDate}>{formatDate(new Date(playlist.datetime))}</time>
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