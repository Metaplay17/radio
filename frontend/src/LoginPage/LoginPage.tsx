import { useState } from 'react';
import styles from './LoginPage.module.css';
import { useNavigate } from 'react-router-dom';
import type { LoginResponse } from '../interfaces';
import { InfoModal } from '../InfoModal/InfoModal';

const Login = () => {
  const navigate = useNavigate();
  const API_URL = import.meta.env.VITE_API_URL;

  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>('');

  const login = async () => {
    setIsLoading(true);
    try {
        const response = await fetch(API_URL + '/api/public/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                username: username, 
                password: password
            })
        });
        if (response.status == 200) {
            const json : LoginResponse = await response.json();
            localStorage.setItem('token', json.token);
            localStorage.setItem('username', username);
            localStorage.setItem('privilege', json.privilege);
            switch (json.privilege) {
                case 'ROLE_REDACTOR':
                    navigate('/redactor');
                    break;
                case 'ROLE_CONTENT_MANAGER':
                    navigate('/content-manager');
                    break;
                case 'ROLE_LICENSE_MANAGER':
                    navigate('/license-manager');
                    break;
                case 'ROLE_ADMIN':
                    navigate('/admin');
                    break;
                default:
                    setModalMessage("Получена неизвестная роль, обратитесь в поддержку");
                    setIsModalOpen(true);
                    break;
            }
        }
        else if (response.status == 403) {
            setModalMessage("Проверьте правильность логина и пароля");
            setIsModalOpen(true);
        }
        else {
            setModalMessage("Ошибка сервера");
            setIsModalOpen(true);
        }
    } catch (e) {
        setModalMessage("Проверьте подключение к интернету");
        setIsModalOpen(true);
    }
    setIsLoading(false);
  }

  return (
    <div className={styles.container}>
        <InfoModal isOpen={isModalOpen} message={modalMessage} title="Информация" onClose={() => {setIsModalOpen(false)}}  />
      <div className={styles.card}>
        <h1 className={styles.title}>Система персонализированных рекомендаций музыкального контента</h1>
        <h2 className={styles.subtitle}>Вход</h2>
        
        <form className={styles.form}>
          <div className={styles.inputGroup}>
            <label htmlFor="username" className={styles.label}>
              Имя пользователя
            </label>
            <input
              id="username"
              type="text"
              className={styles.input}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Введите логин"
              required
              autoComplete="username"
            />
          </div>

          <div className={styles.inputGroup}>
            <label htmlFor="password" className={styles.label}>
              Пароль
            </label>
            <input
              id="password"
              type="password"
              className={styles.input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Введите пароль"
              required
              autoComplete="current-password"
            />
          </div>

          <button
            onClick={login} 
            className={styles.button}
            disabled={isLoading}
          >
            {isLoading ? 'Вход...' : 'Войти'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;