import React, { useState, useEffect } from 'react';
import styles from './FormReportModal.module.css';
import { ErrorResponseException, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';
import type { InteractionTypeDto, TrackDto } from '../interfaces';

export interface AnalyticalReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
}

export function FormReportModal({
  isOpen,
  onClose,
  title = 'Аналитический отчет'
}: AnalyticalReportModalProps) {
  const navigate = useNavigate();

  // Состояние формы
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [trackQuery, setTrackQuery] = useState('');
  const [count, setCount] = useState(0);
  const [interactionTypeQuery, setInteractionTypeQuery] = useState('');
  
  // Справочник типов взаимодействия
  const [interactionTypes, setInteractionTypes] = useState<InteractionTypeDto[]>([]);
  const [tracks, setTracks] = useState<TrackDto[]>([])
  
  const [isLoading, setIsLoading] = useState(false);
  const [isInfoModalOpen, setIsInfoModalOpen] = useState(false);
  const [infoMessage, setInfoMessage] = useState('');

  // Загрузка типов взаимодействия при открытии
  const fetchInteractionTypes = async () => {
    try {
      const types = await makeSafeAuthGet('/api/interaction-types', navigate);
      setInteractionTypes(types);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setInfoMessage(err.message);
        setIsInfoModalOpen(true);
      }
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchInteractionTypes();
    } else {
      setDateFrom('');
      setDateTo('');
      setTrackQuery('');
      setInteractionTypeQuery('');
      setIsLoading(false);
    }
  }, [isOpen]);

  // Поиск трека по Enter
  const handleTrackKeyDown = async (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      if (!trackQuery.trim()) return;
      setIsLoading(true);
      try {
        const fetchedTracks : TrackDto[] = await makeSafeAuthGet(`/api/tracks?namePattern=${trackQuery}&lastId=${0}&isLicensedOnly=false`, navigate);
        setTracks(fetchedTracks);
      } catch (err) {
        if (err instanceof ErrorResponseException) {
          setInfoMessage(err.message);
          setIsInfoModalOpen(true);
        }
      } finally {
        setIsLoading(false);
      }
    }
  };

  // Скачивание отчета
  const handleDownload = async () => {
    if (!dateFrom || !dateTo) {
      setInfoMessage('Укажите диапазон дат (с и по)');
      setIsInfoModalOpen(true);
      return;
    }

    setIsLoading(true);
    let trackTitle = null;
    let artistName = null;
    try {
      if (trackQuery.trim()) {
        trackTitle = trackQuery.split(" - ")[0] || null;
        artistName = trackQuery.split(" - ")[1] || null;
      }
      const response = await makeSafeAuthGet(`/api/interactions?interactionType=${interactionTypeQuery}&msecFrom=${new Date(dateFrom).getTime()}&msecTo=${new Date(dateTo).getTime()}&trackTitle=${trackTitle}&artistName=${artistName}&count=${count}`, navigate);
      const blob = new Blob([JSON.stringify(response)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `report_${trackQuery.length != 0 ? trackQuery : "ALL_TRACKS"}_${dateFrom}_to_${dateTo}_${interactionTypeQuery.length != 0 ? interactionTypeQuery : "ALL"}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

      setInfoMessage('Отчет успешно сформирован и сохранен');
      setIsInfoModalOpen(true);
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setInfoMessage(err.message);
      } else {
        setInfoMessage('Ошибка при скачивании отчета');
      }
      setIsInfoModalOpen(true);
      console.error('Ошибка скачивания:', err);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div
      className={styles.overlay}
      onClick={onClose}
      role="dialog"
      aria-modal="true"
    >
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <InfoModal 
          title="Информация" 
          isOpen={isInfoModalOpen} 
          onClose={() => setIsInfoModalOpen(false)} 
          message={infoMessage} 
        />

        <header className={styles.header}>
          <h2 className={styles.title}>{title}</h2>
          <button type="button" onClick={onClose} className={styles.closeBtn} aria-label="Закрыть">
            ×
          </button>
        </header>

        <div className={styles.body}>
          <div className={styles.row}>
            <div className={styles.formGroup}>
              <label htmlFor="date-from" className={styles.label}>Дата (с)</label>
              <input
                id="date-from"
                type="date"
                value={dateFrom}
                onChange={(e) => setDateFrom(e.target.value)}
                className={styles.input}
                max={dateTo || undefined}
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="date-to" className={styles.label}>Дата (по)</label>
              <input
                id="date-to"
                type="date"
                value={dateTo}
                onChange={(e) => setDateTo(e.target.value)}
                className={styles.input}
                min={dateFrom || undefined}
              />
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="track-select" className={styles.label}>Трек</label>
            <input
              id="track-select"
              type="text"
              list="tracks"
              value={trackQuery}
              onChange={(e) => { setTrackQuery(e.target.value); }}
              onKeyDown={handleTrackKeyDown}
              placeholder="Введите название и нажмите Enter"
              className={`${styles.input}`}
              autoComplete="off"
            />
            <datalist id="tracks">
              {tracks.map(track => (
                <option value={track.title + " - " + track.artistName} />
              ))}
            </datalist>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="interaction-type" className={styles.label}>Тип взаимодействия</label>
            <input
              id="interaction-type"
              list="interaction-options"
              value={interactionTypeQuery}
              onChange={(e) => setInteractionTypeQuery(e.target.value)}
              placeholder="Начните вводить или выберите..."
              className={styles.input}
            />
            <datalist id="interaction-options">
              {interactionTypes.map(type => (
                <option value={type.name} />
              ))}
            </datalist>
          </div>

        <div className={styles.formGroup}>
            <label htmlFor="count" className={styles.label}>Количество записей</label>
            <input
              id="count"
              value={count}
              type="number"
              min="1"
              max="10000"
              onChange={(e) => setCount(Number(e.target.value))}
              className={styles.input}
            />
          </div>
        </div>

        <footer className={styles.footer}>
          <button
            type="button"
            onClick={handleDownload}
            className={styles.downloadBtn}
            disabled={isLoading || !dateFrom || !dateTo}
          >
            {isLoading ? 'Формирование...' : 'Скачать отчет'}
          </button>
        </footer>
      </div>
    </div>
  );
}