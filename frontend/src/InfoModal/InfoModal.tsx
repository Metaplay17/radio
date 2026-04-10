import { useEffect, useCallback } from 'react';
import styles from './InfoModal.module.css';

export interface InfoModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message: string;
  okButtonText?: string;
}

export function InfoModal({
  isOpen,
  onClose,
  title = 'Информация',
  message,
  okButtonText = 'ОК'
}: InfoModalProps) {
  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key === 'Escape') {
      onClose();
    }
  }, [onClose]);

  useEffect(() => {
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      return () => document.removeEventListener('keydown', handleKeyDown);
    }
  }, [isOpen, handleKeyDown]);

  if (!isOpen) return null;

  return (
    <div
      className={styles.overlay}
      onClick={onClose}
      role="dialog"
      aria-modal="true"
    >
      {/* stopPropagation предотвращает закрытие при клике внутри окна */}
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h2 className={styles.title}>
          {title}
        </h2>
        <div className={styles.content}>{message}</div>
        <button
          type="button"
          className={styles.button}
          onClick={onClose}
        >
          {okButtonText}
        </button>
      </div>
    </div>
  );
}