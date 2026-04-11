import React, { useState, useEffect } from 'react';
import styles from './ArtistPage.module.css';
import type { ArtistDto } from '../interfaces';
import { ErrorResponseException, logout, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

const ITEMS_PER_PAGE = 10;

export function ArtistDatabasePage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  if (!['ROLE_REDACTOR', 'ROLE_CONTENT_MANAGER', 'ROLE_ANALYST'].includes(localStorage.getItem('privilege') || '')) {
    window.location.href = '/login';
    return null;
  }

  const [nameFilter, setNameFilter] = useState('');

  const [lastId, setLastId] = useState(0);
  const [artists, setArtists] = useState<ArtistDto[]>([]);
  const [nextArtists, setNextArtists] = useState<ArtistDto[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  const fetchArtists = async (cursorId: number, pattern: string) => {
    setIsLoading(true);
    try {
      const current: ArtistDto[] = await makeSafeAuthGet(
        `/api/artists?lastId=${cursorId}&namePattern=${pattern}`,
        navigate
      );
      setArtists(current);

      const next: ArtistDto[] = await makeSafeAuthGet(
        `/api/artists?lastId=${cursorId + ITEMS_PER_PAGE}&namePattern=${pattern}`,
        navigate
      );
      setNextArtists(next);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка загрузки исполнителей:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchArtists(0, '');
  }, []);

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNameFilter(e.target.value);
  };

  const handleNameSubmit = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      setLastId(0);
      fetchArtists(0, nameFilter);
    }
  };

  const handleSearchClick = () => {
    setLastId(0);
    fetchArtists(0, nameFilter);
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
    fetchArtists(0, '');
  };

  useEffect(() => {
    fetchArtists(lastId, nameFilter);
  }, [lastId]);

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
        <h1 className={styles.mainTitle}>База исполнителей</h1>
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
            <label htmlFor="artist-search" className={styles.filterLabel}>Поиск по названию</label>
            <input
              id="artist-search"
              type="text"
              value={nameFilter}
              onChange={handleNameChange}
              onKeyDown={handleNameSubmit}
              placeholder="Например: The Beatles..."
              className={styles.input}
              disabled={isLoading}
            />
            <span className={styles.hint}>Нажмите Enter для поиска</span>
          </div>

          <button 
            onClick={handleSearchClick} 
            className={styles.searchBtn}
            disabled={isLoading}
          >
            {isLoading ? 'Поиск...' : 'Найти'}
          </button>

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

        {/* Список исполнителей */}
        <section className={styles.listSection}>
          {isLoading && artists.length === 0 ? (
            <div className={styles.loadingState}>
              <div className={styles.spinner} />
              <p>Загрузка исполнителей...</p>
            </div>
          ) : artists.length === 0 ? (
            <div className={styles.emptyState}>
              <p>{nameFilter ? `Исполнители с "${nameFilter}" не найдены` : 'Исполнители отсутствуют'}</p>
              {nameFilter && (
                <button onClick={handleClearFilters} className={styles.clearFilterBtn}>
                  Сбросить поиск
                </button>
              )}
            </div>
          ) : (
            <div className={styles.artistGrid}>
              {artists.map(artist => (
                <div
                  key={artist.id}
                  className={styles.artistItem}
                  role="listitem"
                >
                  <div className={styles.artistInfo}>
                    <span className={styles.artistName}>{artist.name}</span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Пагинация */}
          {artists.length > 0 && (
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
                disabled={nextArtists.length === 0 || isLoading}
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