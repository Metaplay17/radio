import { useState, useEffect, type KeyboardEvent} from 'react';
import styles from './AnalystPage.module.css';
import type { TrackDto, FeatureTypeDto, FeatureDto, OkResponse } from '../interfaces';
import { ErrorResponseException, logout, makeSafeAuthGet, makeSafeAuthPost, makeSafeAuthDelete } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

const ITEMS_PER_PAGE = 10;

export function AnalystPage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  if (localStorage.getItem('privilege') !== 'ROLE_ANALYST') {
    window.location.href = '/login';
    return null;
  }

  const [newFeatureTrackQuery, setNewFeatureTrackQuery] = useState('');
  const [newFeatureTypeQuery, setNewFeatureTypeQuery] = useState('');
  const [newFeatureValue, setNewFeatureValue] = useState('');

  const [newTypeName, setNewTypeName] = useState('');

  const [filterTrackQuery, setFilterTrackQuery] = useState('');
  const [lastId, setLastId] = useState(0);
  const [Features, setFeatures] = useState<FeatureDto[]>([]);
  const [nextFeatures, setNextFeatures] = useState<FeatureDto[]>([]);

  const [tracks, setTracks] = useState<TrackDto[]>([]);
  const [featureTypes, setFeatureTypes] = useState<FeatureTypeDto[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const fetchFeatureTypes = async () => {
    try {
      const fetchedFeatureTypes = await makeSafeAuthGet('/api/audio-feature-types', navigate);
      setFeatureTypes(fetchedFeatureTypes);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка загрузки справочников:', err);
    }
  };

  const fetchFeatures = async (cursorId: number, trackQuery: string) => {
    setIsLoading(true);
    try {
      const trackName = trackQuery.trim().split(" - ")[0] || null;
      const artistName = trackQuery.trim().split(" - ")[1] || null;
      const current = await makeSafeAuthGet(`/api/audio-features?trackTitle=${trackName}&artistName=${artistName}&lastId=${cursorId}`, navigate);
      setFeatures(current);

      const next = await makeSafeAuthGet(`/api/audio-features?trackTitle=${trackName}&artistName=${artistName}&lastId=${cursorId + 10}`, navigate);
      setNextFeatures(next);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
      console.error('Ошибка загрузки признаков:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchFeatureTypes();
  }, []);

  const handleAddFeature = async () => {
    const valueNum = parseFloat(newFeatureValue);
    if (!newFeatureTrackQuery.trim() || !newFeatureTypeQuery.trim() || isNaN(valueNum) || valueNum < 0 || valueNum > 1) {
      setModalMessage('Заполните все поля корректно. Значение должно быть числом от 0 до 1');
      setIsModalOpen(true);
      return;
    }

    const featureType = featureTypes.find(t => t.name.toLowerCase() === newFeatureTypeQuery.toLowerCase());

    if (!newFeatureTrackQuery || !featureType) {
      setModalMessage('Выберите существующий трек и тип признака из списка');
      setIsModalOpen(true);
      return;
    }

    setIsLoading(true);
    try {
      await makeSafeAuthPost('/api/audio-features', navigate, {
        trackTitle: newFeatureTrackQuery.trim().split(" - ")[0] || null,
        artistName: newFeatureTrackQuery.trim().split(" - ")[1] || null,
        featureTypeId: featureType.id,
        value: valueNum
      });
      setModalMessage('Признак успешно добавлен');
      setIsModalOpen(true);

      setNewFeatureTrackQuery('');
      setNewFeatureTypeQuery('');
      setNewFeatureValue('');
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
      } else {
        setModalMessage('Ошибка при добавлении признака');
      }
      setIsModalOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddFeatureType = async () => {
    if (!newTypeName.trim()) return;

    setIsLoading(true);
    try {
      await makeSafeAuthPost('/api/audio-feature-types', navigate, {
        name: newTypeName.trim()
      });
      setModalMessage('Тип признака успешно добавлен');
      setIsModalOpen(true);
      setNewTypeName('');

      const updated = await makeSafeAuthGet('/api/audio-feature-types', navigate);
      setFeatureTypes(updated);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleFindFeatures = () => {
    setLastId(0);
    fetchFeatures(0, filterTrackQuery);
  };

  const handleDeleteFeature = async (trackId : number, featureTypeId : number) => {
    try {
      const json : OkResponse = await makeSafeAuthDelete(`/api/audio-features`, navigate, {
        trackId: trackId,
        featureTypeId: featureTypeId
      });
      setModalMessage(json.message);
      setIsModalOpen(true);
      fetchFeatures(lastId, filterTrackQuery);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
        setIsModalOpen(true);
      }
    }
  };

  const handlePageNext = () => setLastId(prev => prev + ITEMS_PER_PAGE);
  const handlePagePrev = () => setLastId(prev => Math.max(0, prev - ITEMS_PER_PAGE));

  useEffect(() => {
    if (lastId !== 0) fetchFeatures(lastId, filterTrackQuery);
  }, [lastId]);

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

  const searchTracks = async (e : KeyboardEvent<HTMLInputElement>) => {
    if (e.key == "Enter") {
        await fetchTracks(filterTrackQuery);
    }
  };

  return (
    <div className={styles.page}>
      <InfoModal title="Информация" isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} message={modalMessage} />

      {/* Верхняя панель */}
      <header className={styles.header}>
        <h1 className={styles.mainTitle}>Страница аналитика</h1>
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
      </nav>

      {/* Основной контент */}
      <main className={styles.content}>
        {/* Левая панель: Добавление признака */}
        <aside className={styles.leftPanel}>
          <h2 className={styles.panelTitle}>Добавить признак</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="add-track" className={styles.label}>Трек</label>
              <input
                id="add-track"
                list="add-track-options"
                value={newFeatureTrackQuery}
                onChange={(e) => setNewFeatureTrackQuery(e.target.value)}
                onKeyDown={(e) => searchTracks(e)}
                placeholder="Начните вводить..."
                className={styles.input}
                required
              />
              <datalist id="add-track-options">
                {tracks.map(t => <option key={t.id} value={t.title + " - " + t.artistName} />)}
              </datalist>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="add-type" className={styles.label}>Тип признака</label>
              <input
                id="add-type"
                list="add-type-options"
                value={newFeatureTypeQuery}
                onChange={(e) => setNewFeatureTypeQuery(e.target.value)}
                placeholder="Начните вводить..."
                className={styles.input}
                required
              />
              <datalist id="add-type-options">
                {featureTypes.map(type => <option key={type.id} value={type.name} />)}
              </datalist>
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="add-value" className={styles.label}>Значение (число)</label>
              <input
                id="add-value"
                type="number"
                step="0.01"
                value={newFeatureValue}
                onChange={(e) => setNewFeatureValue(e.target.value)}
                placeholder="Например: 0.85"
                className={styles.input}
                required
              />
            </div>

            <button onClick={handleAddFeature} className={styles.primaryBtn} disabled={isLoading}>
              {isLoading ? 'Добавление...' : 'Добавить'}
            </button>
          </div>
        </aside>

        {/* Центральная панель: Обзор признаков */}
        <section className={styles.centerPanel}>
          <div className={styles.filterBar}>
            <div className={styles.filterGroup}>
              <label htmlFor="filter-track" className={styles.filterLabel}>Поиск по треку</label>
              <input
                id="filter-track"
                list="filter-track-options"
                value={filterTrackQuery}
                onChange={(e) => setFilterTrackQuery(e.target.value)}
                onKeyDown={(e) => searchTracks(e)}
                placeholder="Начните вводить..."
                className={styles.input}
              />
              <datalist id="filter-track-options">
                {tracks.map(t => <option key={t.id} value={t.title + " - " + t.artistName} />)}
              </datalist>
            </div>
            <button onClick={handleFindFeatures} className={styles.searchBtn} disabled={isLoading}>
              {isLoading ? 'Поиск...' : 'Найти'}
            </button>
          </div>

          <div className={styles.FeaturesList} role="list">
            {isLoading && Features.length === 0 ? (
              <div className={styles.loadingState}><div className={styles.spinner} /><p>Загрузка...</p></div>
            ) : Features.length === 0 ? (
              <div className={styles.emptyState}><p>Признаки не найдены</p></div>
            ) : (
              Features.map(Feature => (
                <div className={styles.FeatureItem} role="listitem">
                  <div className={styles.FeatureInfo}>
                    <span className={styles.FeatureType}>{Feature.name}</span>
                    <span className={styles.FeatureValue}>{Feature.value}</span>
                  </div>
                  <div className={styles.FeatureTrack}>
                    <span className={styles.trackName}>{Feature.track.title}</span>
                    <span className={styles.trackArtist}>{Feature.track.artistName}</span>
                  </div>
                  <button onClick={() => handleDeleteFeature(Feature.track.id, Feature.featureTypeId)} className={styles.deleteBtn} aria-label="Удалить признак">
                    Удалить
                  </button>
                </div>
              ))
            )}
          </div>

          {/* Пагинация */}
          {Features.length > 0 && (
            <nav className={styles.pagination} aria-label="Навигация по страницам">
              <button onClick={handlePagePrev} disabled={lastId === 0 || isLoading} className={styles.pageBtn} aria-label="Предыдущая страница">← Назад</button>
              <span className={styles.pageInfo}>Страница {Math.floor(lastId / ITEMS_PER_PAGE) + 1}</span>
              <button onClick={handlePageNext} disabled={nextFeatures.length === 0 || isLoading} className={styles.pageBtn} aria-label="Следующая страница">Вперёд →</button>
            </nav>
          )}
        </section>

        {/* Правая панель: Добавление типа признака */}
        <aside className={styles.rightPanel}>
          <h2 className={styles.panelTitle}>Новый тип признака</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="new-type-name" className={styles.label}>Название</label>
              <input
                id="new-type-name"
                type="text"
                value={newTypeName}
                onChange={(e) => setNewTypeName(e.target.value)}
                placeholder="Например: Энергичность"
                className={styles.input}
                required
              />
            </div>
            <button onClick={handleAddFeatureType} className={styles.primaryBtn} disabled={isLoading || !newTypeName.trim()}>
              {isLoading ? 'Создание...' : 'Добавить'}
            </button>
          </div>
        </aside>
      </main>
    </div>
  );
}