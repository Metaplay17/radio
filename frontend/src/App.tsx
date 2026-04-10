import './App.css'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import LoginPage from './LoginPage/LoginPage.tsx'
import { RedactorPage } from './RedactorPage/Redactor.tsx'
import { PlaylistHistoryPage } from './RedactorPage/PlaylistHistory.tsx'

function App() {

  return (
      <BrowserRouter>
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/redactor" element={<RedactorPage />} />
            <Route path="/redactor/playlist-history" element={<PlaylistHistoryPage />} />
        </Routes>
      </BrowserRouter>
  )
}

export default App
