import React, { useState, useEffect, type KeyboardEvent } from 'react';
import styles from './LicenseManagerPage.module.css';
import type { TrackDto, ArtistDto, LicenseDto } from '../interfaces';
import { ErrorResponseException, logout, makeSafeAuthGet, makeSafeAuthPost } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

const ITEMS_PER_PAGE = 10;

export function LicensorPage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  if (localStorage.getItem('privilege') !== 'ROLE_LICENSE_MANAGER') {
    window.location.href = '/login';
    return null;
  }

  const [newLicenseTrackQuery, setNewLicenseTrackQuery] = useState('');
  const [newLicenseStartDate, setNewLicenseStartDate] = useState('');
  const [newLicenseDurationDays, setNewLicenseDurationDays] = useState('');

  const [filterTrackQuery, setFilterTrackQuery] = useState('');
  const [filterArtistQuery, setFilterArtistQuery] = useState('');
  const [filterExpired, setFilterExpired] = useState(false);
  const [filterExpiringSoon, setFilterExpiringSoon] = useState(false);

  const [lastId, setLastId] = useState(0);
  const [licenses, setLicenses] = useState<LicenseDto[]>([]);
  const [nextLicenses, setNextLicenses] = useState<LicenseDto[]>([]);

  const [tracks, setTracks] = useState<TrackDto[]>([]);
  const [artists, setArtists] = useState<ArtistDto[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const fetchLicenses = async (cursorId: number) => {
    setIsLoading(true);
    try {
      const current: LicenseDto[] = await makeSafeAuthGet(
        `/api/licenses?type=${filterExpired || filterExpiringSoon ? (filterExpired ? "EXPIRED" : "WARNING") : "ACTIVE"}&trackTitle=${filterTrackQuery}&artistName=${filterArtistQuery}&lastId=${cursorId}`,
        navigate
      );
      setLicenses(current);

      // Предзагрузка следующей страницы
      const next: LicenseDto[] = await makeSafeAuthGet(
        `/api/licenses?type=${filterExpired || filterExpiringSoon ? (filterExpired ? "EXPIRED" : "WARNING") : "ACTIVE"}&trackTitle=${filterTrackQuery}&artistName=${filterArtistQuery}&lastId=${cursorId + ITEMS_PER_PAGE}`,
        navigate
      );
      setNextLicenses(next);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка загрузки лицензий:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchTracks = async (namePattern : string) => {
    setIsLoading(true);
    try {
        const current = await makeSafeAuthGet(
        `/api/tracks?lastId=${0}&isLicensedOnly=false&genre=&titlePattern=${namePattern}&artist=`,
        navigate
        );
        setTracks(current);
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

    const fetchArtists = async (pattern: string) => {
      setIsLoading(true);
      try {
        const current: ArtistDto[] = await makeSafeAuthGet(
          `/api/artists?lastId=${0}&namePattern=${pattern}`,
          navigate
        );
        setArtists(current);
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

  const searchTracks = async (e : KeyboardEvent<HTMLInputElement>) => {
    if (e.key == "Enter") {
        await fetchTracks(filterTrackQuery);
    }
  }

  const searchArtists = async (e : KeyboardEvent<HTMLInputElement>) => {
    if (e.key == "Enter") {
        await fetchArtists(filterArtistQuery);
    }
  }

  useEffect(() => {
    fetchLicenses(lastId);
  }, []);

  useEffect(() => {
    setLastId(0);
    fetchLicenses(0);
  }, [filterTrackQuery, filterArtistQuery, filterExpired, filterExpiringSoon]);

  useEffect(() => {
    fetchLicenses(lastId);
  }, [lastId]);

  const handleAddLicense = async () => {
    if (!newLicenseTrackQuery.trim() || !newLicenseStartDate || !newLicenseDurationDays) {
      setModalMessage('Заполните все поля формы');
      setIsModalOpen(true);
      return;
    }

    if (!filterTrackQuery.trim()) {
      setModalMessage('Выберите трек из списка');
      setIsModalOpen(true);
      return;
    }

    setIsLoading(true);
    try {
      await makeSafeAuthPost('/api/licenses', navigate, {
        trackSignature: {
            title: filterTrackQuery,
            artistName: filterArtistQuery
        },
        registered: new Date(newLicenseStartDate),
        duration: newLicenseDurationDays
      });
      setModalMessage('Лицензия успешно добавлена');
      setIsModalOpen(true);

      setNewLicenseTrackQuery('');
      setNewLicenseStartDate('');
      setNewLicenseDurationDays('');

      setLastId(0);
      fetchLicenses(0);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка добавления лицензии:', err);
    } finally {
      setIsLoading(false);
    }
  };

  // ========== Обработчики фильтров ==========
  const handleFilterTrackChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterTrackQuery(e.target.value);
  };

  const handleFilterArtistChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterArtistQuery(e.target.value);
  };

  const handleFilterExpiredChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterExpired(e.target.checked);
  };

  const handleFilterExpiringSoonChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterExpiringSoon(e.target.checked);
  };

  const handleClearFilters = () => {
    setFilterTrackQuery('');
    setFilterArtistQuery('');
    setFilterExpired(false);
    setFilterExpiringSoon(false);
    setLastId(0);
  };

  const handlePageNext = () => {
    setLastId(prev => prev + ITEMS_PER_PAGE);
  };

  const handlePagePrev = () => {
    setLastId(prev => Math.max(0, prev - ITEMS_PER_PAGE));
  };

  const formatDate = (isoDate: string | Date): string => {
    return new Date(isoDate).toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  };

  const calculateEndDate = (startDate: string, durationDays: number): string => {
    const date = new Date(startDate);
    date.setDate(date.getDate() + durationDays);
    return formatDate(date);
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
        <h1 className={styles.mainTitle}>Страница лицензиара</h1>
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
        <button onClick={() => navigate('/artists')} className={styles.navBtn}>
          Обзор исполнителей
        </button>
      </nav>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Левая панель: Добавление лицензии */}
        <aside className={styles.leftPanel}>
          <h2 className={styles.panelTitle}>Добавить лицензию</h2>
          <div className={styles.addForm}>
            <div className={styles.formGroup}>
              <label htmlFor="add-track" className={styles.label}>Трек</label>
              <input
                id="add-track"
                list="add-track-options"
                value={newLicenseTrackQuery}
                onChange={(e) => setNewLicenseTrackQuery(e.target.value)}
                placeholder="Начните вводить..."
                className={styles.input}
                required
              />
              <datalist id="add-track-options">
                {tracks.map(track => (
                  <option key={track.id} value={track.title} />
                ))}
              </datalist>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="add-start-date" className={styles.label}>Дата начала</label>
              <input
                id="add-start-date"
                type="date"
                value={newLicenseStartDate}
                onChange={(e) => setNewLicenseStartDate(e.target.value)}
                className={styles.input}
                required
                min={new Date().toISOString().split('T')[0]}
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="add-duration" className={styles.label}>Длительность (дней)</label>
              <input
                id="add-duration"
                type="number"
                min="1"
                max="3650"
                value={newLicenseDurationDays}
                onChange={(e) => setNewLicenseDurationDays(e.target.value)}
                placeholder="Например: 365"
                className={styles.input}
                required
              />
            </div>

            <button
              onClick={handleAddLicense}
              className={styles.primaryBtn}
              disabled={isLoading || !newLicenseTrackQuery || !newLicenseStartDate || !newLicenseDurationDays}
            >
              {isLoading ? 'Добавление...' : 'Добавить'}
            </button>
          </div>
        </aside>

        {/* Правая панель: Список лицензий с фильтрами */}
        <section className={styles.rightPanel}>
          {/* Фильтры */}
          <div className={styles.filterBar}>
            <div className={styles.filterGroup}>
              <label htmlFor="filter-track" className={styles.filterLabel}>Трек</label>
              <input
                id="filter-track"
                list="filter-track-options"
                value={filterTrackQuery}
                onChange={handleFilterTrackChange}
                onKeyDown={searchTracks}
                placeholder="Начните вводить..."
                className={styles.input}
              />
              <datalist id="filter-track-options">
                {tracks.map(track => (
                  <option key={track.id} value={track.title} />
                ))}
              </datalist>
            </div>

            <div className={styles.filterGroup}>
              <label htmlFor="filter-artist" className={styles.filterLabel}>Исполнитель</label>
              <input
                id="filter-artist"
                list="filter-artist-options"
                value={filterArtistQuery}
                onChange={handleFilterArtistChange}
                onKeyDown={searchArtists}
                placeholder="Начните вводить..."
                className={styles.input}
              />
              <datalist id="filter-artist-options">
                {artists.map(artist => (
                  <option key={artist.id} value={artist.name} />
                ))}
              </datalist>
            </div>

            <label className={styles.checkboxLabel}>
              <input
                type="checkbox"
                checked={filterExpired}
                onChange={handleFilterExpiredChange}
                className={styles.checkbox}
              />
              <span>Истекшие</span>
            </label>

            <label className={styles.checkboxLabel}>
              <input
                type="checkbox"
                checked={filterExpiringSoon}
                onChange={handleFilterExpiringSoonChange}
                className={styles.checkbox}
              />
              <span>Истекают в течение 30 дней</span>
            </label>

            <button onClick={handleClearFilters} className={styles.clearBtn}>
              Сбросить
            </button>

            <button onClick={searchLicenses} className={styles.clearBtn}>
              Найти
            </button>
          </div>

          {/* Список лицензий */}
          <div className={styles.licenseList}>
            {isLoading && licenses.length === 0 ? (
              <div className={styles.loadingState}>
                <div className={styles.spinner} />
                <p>Загрузка лицензий...</p>
              </div>
            ) : licenses.length === 0 ? (
              <div className={styles.emptyState}>
                <p>Лицензии не найдены</p>
                {(filterTrackQuery || filterArtistQuery || filterExpired || filterExpiringSoon) && (
                  <button onClick={handleClearFilters} className={styles.clearFilterBtn}>
                    Сбросить фильтры
                  </button>
                )}
              </div>
            ) : (
              licenses.map(license => {
                return (
                  <div
                    key={license.id}
                    className={`${styles.licenseItem} ${filterExpired ? styles.expired : ''} ${filterExpiringSoon ? styles.expiringSoon : ''}`}
                    role="listitem"
                  >
                    <div className={styles.licenseInfo}>
                      <span className={styles.licenseTrack}>{license.track.title}</span>
                      <span className={styles.licenseArtist}>{license.track.artistName}</span>
                    </div>
                    <div className={styles.licenseMeta}>
                      <div className={styles.licenseDates}>
                        <span className={styles.licenseDateLabel}>Начало:</span>
                        <time className={styles.licenseDate}>{formatDate(license.registered)}</time>
                      </div>
                      <div className={styles.licenseDates}>
                        <span className={styles.licenseDateLabel}>Окончание:</span>
                        <time className={styles.licenseDate}>
                          {calculateEndDate(license.registered, license.duration)}
                        </time>
                      </div>
                      <span className={styles.licenseDuration}>{license.duration} дн.</span>
                    </div>
                    <div className={styles.licenseBadges}>
                      {filterExpired && <span className={styles.badgeExpired}>Истекла</span>}
                      {filterExpiringSoon && <span className={styles.badgeExpiring}>Скоро истекает</span>}
                      {!filterExpired && !filterExpiringSoon && <span className={styles.badgeActive}>Активна</span>}
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Пагинация */}
          {licenses.length > 0 && (
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
                disabled={nextLicenses.length === 0 || isLoading}
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