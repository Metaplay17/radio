import React, { useState, useEffect } from 'react';
import styles from './SelectTrackModal.module.css';
import type { TrackDto } from '../interfaces';
import { ErrorResponseException, makeSafeAuthGet } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';

export interface TrackSelectModalProps {
  /** Флаг открытия окна */
  isOpen: boolean;
  /** Колбэк закрытия */
  onClose: () => void;
  /** Колбэк подтверждения выбора */
  onAdd: (trackName: string) => void;
}

export function SelectTrackModal({
  isOpen,
  onClose,
  onAdd
}: TrackSelectModalProps) {
  const modalId = React.useId();
  const navigate = useNavigate();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  
  const [query, setQuery] = useState('');
  const [tracks, setTracks] = useState<TrackDto[]>([]);

  // Сброс состояния при закрытии
  useEffect(() => {
    if (!isOpen) {
      setQuery('');
    }
  }, [isOpen]);

  // Обработка нажатия Enter для поиска
  const handleKeyDown = async (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      if (!query.trim()) return;
      await fetchTracks(query);
    }
  };

const fetchTracks = async (namePattern : string) => {
    try {
        const current = await makeSafeAuthGet(
        `/api/tracks?lastId=${0}&isLicensedOnly=true&genre=&titlePattern=${namePattern}&artist=`,
        navigate
        );
        setTracks(current);
    } catch (err) {
        if (err instanceof ErrorResponseException) {
            setModalMessage(err.message);
            setIsModalOpen(true);
        }
        console.error('Ошибка загрузки треков:', err);
    }
};

  const handleConfirm = () => {
    if (query.trim()) {
      onAdd(query.trim());
      onClose();
    }
  };

  if (!isOpen) return null;

  return (
    <div
      className={styles.overlay}
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby={modalId}
    >
        <InfoModal
            title="Информация" 
            isOpen={isModalOpen} 
            onClose={() => setIsModalOpen(false)} 
            message={modalMessage} 
        />
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <header className={styles.header}>
          <h2 id={modalId} className={styles.title}>{"Добавление трека"}</h2>
          <button 
            type="button" 
            onClick={onClose} 
            className={styles.closeBtn}
            aria-label="Закрыть окно"
          >
            ×
          </button>
        </header>

        <div className={styles.body}>
          <div className={styles.inputGroup}>
            <label htmlFor="track-select-input" className={styles.label}>Поиск трека</label>
            <input
              id="track-select-input"
              type="text"
              list="track-suggestions"
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
              }}
              onKeyDown={handleKeyDown}
              className={styles.input}
              autoComplete="off"
            />
            <datalist id="track-suggestions">
              {tracks.map((track) => (
                <option value={`${track.title} - ${track.artistName}`} />
              ))}
            </datalist>
            <span className={styles.hint}>Введите название и нажмите Enter</span>
          </div>
        </div>

        <footer className={styles.footer}>
          <button
            type="button"
            onClick={handleConfirm}
            className={styles.addBtn}
            disabled={!query.trim()}
          >
            {'Добавить'}
          </button>
        </footer>
      </div>
    </div>
  );
}