import React, { useState, useEffect, useMemo } from 'react';
import styles from './GenrePage.module.css';
import type { GenreDto } from '../interfaces';
import { ErrorResponseException, logout, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

const ITEMS_PER_PAGE = 10;

export function GenreDatabasePage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  // Проверка прав доступа
  if (!['ROLE_CONTENT_MANAGER'].includes(localStorage.getItem('privilege') || '')) {
    window.location.href = '/login';
    return null;
  }

  // Состояние фильтра
  const [nameFilter, setNameFilter] = useState('');

  // Состояние пагинации (cursor-based)
  const [lastId, setLastId] = useState(0);
  const [allGenres, setAllGenres] = useState<GenreDto[]>([]);
  const [filteredGenres, setFilteredGenres] = useState<GenreDto[]>([]);
  const [pageGenres, setPageGenres] = useState<GenreDto[]>([]);
  const [hasNextPage, setHasNextPage] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  // Загрузка всех жанров один раз при монтировании
  const fetchAllGenres = async () => {
    setIsLoading(true);
    try {
      const genres: GenreDto[] = await makeSafeAuthGet('/api/genres', navigate);
      const sorted = genres.sort((a, b) => a.id - b.id);
      setAllGenres(sorted);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка загрузки жанров:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAllGenres();
  }, []);

  // Клиентская фильтрация по названию (мемоизируем)
  const filtered = useMemo(() => {
    if (!nameFilter.trim()) return allGenres;
    const pattern = nameFilter.toLowerCase();
    return allGenres.filter(g => g.name.toLowerCase().includes(pattern));
  }, [allGenres, nameFilter]);

  // Обновление отфильтрованного списка и пагинации
  useEffect(() => {
    setFilteredGenres(filtered);
    setLastId(0); // Сброс на первую страницу при изменении фильтра
  }, [filtered]);

  // Применение пагинации к отфильтрованным данным
  useEffect(() => {
    const start = lastId;
    const end = lastId + ITEMS_PER_PAGE;
    const current = filteredGenres.slice(start, end);
    const next = filteredGenres.slice(end, end + ITEMS_PER_PAGE);

    setPageGenres(current);
    setHasNextPage(next.length > 0);
  }, [lastId, filteredGenres]);

  // Обработчики
  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNameFilter(e.target.value);
  };

  const handlePageNext = () => {
    setLastId(prev => prev + ITEMS_PER_PAGE);
  };

  const handlePagePrev = () => {
    setLastId(prev => Math.max(0, prev - ITEMS_PER_PAGE));
  };

  const handleClearFilters = () => {
    setNameFilter('');
    setLastId(0);
  };

  return (
    <div className={styles.page}>
      <InfoModal 
        title="Информация" 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        message={modalMessage} 
      />

      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>База жанров</h1>
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
            <label htmlFor="genre-search" className={styles.filterLabel}>Поиск по названию</label>
            <input
              id="genre-search"
              type="text"
              value={nameFilter}
              onChange={handleNameChange}
              placeholder="Например: рок, джаз..."
              className={styles.input}
              disabled={isLoading || allGenres.length === 0}
            />
          </div>

          <button 
            onClick={handleClearFilters} 
            className={styles.clearBtn}
            disabled={!nameFilter && lastId === 0}
          >
            Сбросить
          </button>

          <button onClick={() => navigate(-1)} className={styles.backBtn}>
            Назад
          </button>
        </section>

        {/* Список жанров */}
        <section className={styles.listSection}>
          {isLoading && allGenres.length === 0 ? (
            <div className={styles.loadingState}>
              <div className={styles.spinner} />
              <p>Загрузка жанров...</p>
            </div>
          ) : pageGenres.length === 0 ? (
            <div className={styles.emptyState}>
              <p>{nameFilter ? 'Жанры не найдены' : 'Жанры отсутствуют'}</p>
              {nameFilter && (
                <button onClick={handleClearFilters} className={styles.clearFilterBtn}>
                  Сбросить поиск
                </button>
              )}
            </div>
          ) : (
            <div className={styles.genreGrid}>
              {pageGenres.map(genre => (
                <div
                  key={genre.id}
                  className={styles.genreItem}
                  role="listitem"
                >
                  <div className={styles.genreInfo}>
                    <span className={styles.genreName}>{genre.name}</span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Пагинация */}
          {pageGenres.length > 0 && (
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
                Страница {Math.floor(lastId / ITEMS_PER_PAGE) + 1} из {Math.ceil(filteredGenres.length / ITEMS_PER_PAGE) || 1}
              </span>

              <button
                onClick={handlePageNext}
                disabled={!hasNextPage || isLoading}
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