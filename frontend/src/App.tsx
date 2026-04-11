import './App.css'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import LoginPage from './LoginPage/LoginPage.tsx'
import { RedactorPage } from './RedactorPage/Redactor.tsx'
import { PlaylistHistoryPage } from './RedactorPage/PlaylistHistory.tsx'
import { TrackDatabasePage } from './TrackComponent/TrackPage.tsx'
import { ContentManagerPage } from './ContentManagerPage/ContentManager.tsx'
import { GenreDatabasePage } from './GenreComponent/GenrePage.tsx'

function App() {

  return (
      <BrowserRouter>
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/redactor" element={<RedactorPage />} />
            <Route path="/content-manager" element={<ContentManagerPage />} />
            <Route path="/redactor/playlist-history" element={<PlaylistHistoryPage />} />
            <Route path="/tracks" element={<TrackDatabasePage />} />
            <Route path="/genres" element={<GenreDatabasePage />} />
        </Routes>
      </BrowserRouter>
  )
}

export default App
