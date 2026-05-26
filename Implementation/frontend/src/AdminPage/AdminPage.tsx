import React, { useState } from 'react';
import styles from './AdminPage.module.css';
import { ErrorResponseException, logout, makeSafeAuthPost } from '../utils';
import { useNavigate } from 'react-router-dom';
import { InfoModal } from '../InfoModal/InfoModal';
import type { OkResponse } from '../interfaces';

export interface CreateUserPayload {
  username: string;
  email: string;
  password: string;
  privilegeLevel: number;
}

const PRIVILEGE_LEVELS = [
  { value: 5, label: 'Аналитик', desc: 'Только просмотр отчетов и статистики' },
  { value: 10, label: 'Лицензиар', desc: 'Управление лицензиями' },
  { value: 25, label: 'Контент-менеджер', desc: 'Добавление треков, жанров, исполнтелей' },
  { value: 50, label: 'Редактор', desc: 'Управление плейлистами' },
  { value: 100, label: 'Администратор', desc: 'Создание пользователей' },
] as const;

export function AdminPage() {
  const username = localStorage.getItem("username");
  const navigate = useNavigate();

  // Проверка прав доступа
  if (localStorage.getItem('privilege') !== 'ROLE_ADMIN') {
    window.location.href = '/login';
    return null;
  }

  // Состояние формы
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    privilegeLevel: ''
  });

  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handlePrivilegeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setForm(prev => ({ ...prev, privilegeLevel: e.target.value }));
  }

  const validateForm = (): boolean => {
    if (!form.username.trim()) {
      setModalMessage('Имя пользователя обязательно');
      setIsModalOpen(true);
      return false;
    }
    if (!form.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      setModalMessage('Введите корректный email');
      setIsModalOpen(true);
      return false;
    }
    if (form.password.length < 6) {
      setModalMessage('Пароль должен содержать минимум 6 символов');
      setIsModalOpen(true);
      return false;
    }
    const level = Number(form.privilegeLevel);
    if (isNaN(level) || level < 1 || level > 100) {
      setModalMessage('Уровень привилегий должен быть числом от 1 до 100');
      setIsModalOpen(true);
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      const payload: CreateUserPayload = {
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
        privilegeLevel: parseInt(form.privilegeLevel, 10)
      };
      const json : OkResponse = await makeSafeAuthPost('/api/admin/users', navigate, payload);
      setModalMessage(json.message);
      setIsModalOpen(true);
      setForm({ username: '', email: '', password: '', privilegeLevel: '' });
    } catch (err) {
      if (err instanceof ErrorResponseException) {
        setModalMessage(err.message);
      } else {
        setModalMessage('Ошибка при создании пользователя');
      }
      setIsModalOpen(true);
      console.error('Ошибка создания пользователя:', err);
    } finally {
      setIsLoading(false);
    }
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
        <h1 className={styles.mainTitle}>Страница администратора</h1>
        <div className={styles.userBlock}>
          <span className={styles.username}>{username}</span>
          <button onClick={() => logout(navigate)} className={styles.logoutBtn}>Выход</button>
        </div>
      </header>

      {/* Основной контент */}
      <main className={styles.content}>
        <section className={styles.card}>
          <h2 className={styles.cardTitle}>Создание нового пользователя</h2>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="username" className={styles.label}>Имя пользователя</label>
              <input
                id="username"
                name="username"
                type="text"
                value={form.username}
                onChange={handleChange}
                placeholder="ivan_petrov"
                className={styles.input}
                required
                autoComplete="username"
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="email" className={styles.label}>Email</label>
              <input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                placeholder="user@example.com"
                className={styles.input}
                required
                autoComplete="email"
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="password" className={styles.label}>Пароль</label>
              <input
                id="password"
                name="password"
                type="password"
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                className={styles.input}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="privilegeLevel" className={styles.label}>Уровень привилегий</label>
              <select
                id="privilegeLevel"
                name="privilegeLevel"
                value={form.privilegeLevel}
                onChange={handlePrivilegeChange}
                className={styles.input}
                required
              >
                {PRIVILEGE_LEVELS.map(level => (
                  <option value={level.value} key={level.value}>{level.label}</option>
                ))}
              </select>
            </div>

            {/* Памятка по уровням */}
            <div className={styles.cheatsheet}>
              <h3 className={styles.cheatsheetTitle}>Справочник уровней доступа</h3>
              <ul className={styles.cheatsheetList}>
                {PRIVILEGE_LEVELS.map(level => (
                  <li key={level.value} className={styles.cheatsheetItem}>
                    <span className={styles.levelBadge}>{level.value}</span>
                    <div className={styles.levelInfo}>
                      <strong>{level.label}</strong>
                      <span className={styles.levelDesc}>{level.desc}</span>
                    </div>
                  </li>
                ))}
              </ul>
            </div>

            <button 
              onClick={handleSubmit}
              className={styles.primaryBtn}
              disabled={isLoading}
            >
              {isLoading ? 'Создание...' : 'Создать'}
            </button>
          </div>
        </section>
      </main>
    </div>
  );
}