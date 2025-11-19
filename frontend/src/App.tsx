/* eslint-disable prettier/prettier */
import './App.css';
import { BrowserRouter, Routes, Route, useNavigate, useParams, Link } from 'react-router-dom';
import { useAllVideos } from './useAllVideos';
import React, { useState, useEffect } from 'react';
import Register from './pages/Register';
import Login from './pages/Login';
import VideoPage from './pages/VideoPage';
import { useAuth } from './context/AuthContext';


function App() {
  const [darkMode, setDarkMode] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem('darkMode');
      return saved ? JSON.parse(saved) : false;
    } catch {
      return false;
    }
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', darkMode ? 'dark' : 'light');
    try {
      localStorage.setItem('darkMode', JSON.stringify(darkMode));
    } catch {
      // ignore
    }
  }, [darkMode]);

  return (
    <BrowserRouter>
      <div className="App">
        <Header darkMode={darkMode} setDarkMode={setDarkMode} />
        <Routes>
          <Route path="/" element={<ContentApp />} />
          <Route path="/search/:query" element={<SearchResults />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/video/:id" element={<VideoPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );


}

// ================= HEADER WITH SEARCH =================
function Header({
                  darkMode,
                  setDarkMode,
                }: {
  darkMode: boolean;
  setDarkMode: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/search/${encodeURIComponent(searchTerm.trim())}`);
      setSearchTerm('');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="App-header">
      <Link to="/" className="logo-link">
        <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
      </Link>
      <h1 className="App-title">ProTube</h1>

      <form onSubmit={handleSearch} className="search-form">
        <input
          type="text"
          placeholder="Search videos..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <button type="submit" className="search-button">
          🔍
        </button>
      </form>

      {!isAuthenticated ? (
        <>
          <button onClick={() => navigate('/login')} className="login-button">
            Iniciar Sesión
          </button>
          <button onClick={() => navigate('/register')} className="register-button">
            Registrarse
          </button>
        </>
      ) : (
        <div className="user-menu">
          <button className="profile-button">
            👤 Perfil
          </button>
          <button onClick={handleLogout} className="logout-button">
            Cerrar Sesión
          </button>
        </div>
      )}

      <button
        className="theme-toggle"
        onClick={() => setDarkMode((s) => !s)}
        aria-label={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
        title={darkMode ? 'Light mode' : 'Dark mode'}
      >
        <span className="theme-icon" aria-hidden>
          {darkMode ? '☀️' : '🌙'}
        </span>
      </button>
    </header>
  );
}

// ================= ALL VIDEOS =================
function ContentApp() {
  const { videos, loading } = useAllVideos();
  //console.log('Videos:', videos);

  if (loading === 'loading' || loading === 'idle') return <div className="loading-spinner">Loading...</div>;
  if (loading === 'error') return <div className="error-message">Error loading videos</div>;

  return <VideoGrid videos={videos} />;
}

// ================= SEARCH RESULTS =================
function SearchResults() {
  const { videos, loading } = useAllVideos();
  const { query } = useParams();

  if (loading === 'loading' || loading === 'idle') return <div className="loading-spinner">Loading...</div>;
  if (loading === 'error') return <div className="error-message">Error loading videos</div>;

  const filteredVideos = videos.filter((video) => video.title?.toLowerCase().includes(query?.toLowerCase() || ''));

  return (
    <div>
      <h2 className="search-title">Search results for: "{query}"</h2>
      {filteredVideos.length > 0 ? (
        <VideoGrid videos={filteredVideos} />
      ) : (
        <div className="error-message">No videos found for "{query}"</div>
      )}
    </div>
  );
}

// ================= REUSABLE VIDEO GRID =================
type VideoItem = {
  id?: number | string;
  title?: string;
  description?: string;
};

function VideoGrid({ videos }: { videos: VideoItem[] }) {
  return (
    <div className="video-grid">
      {videos.map((video, index) => {
        const videoId = video.id || index + 1;
        return (
          <Link
            to={`/video/${videoId}`}
            key={String(videoId) + (video.title ?? '')}
            className="video-card"
            style={{ textDecoration: 'none', color: 'inherit' }}
          >
            <div className="video-thumbnail">
              <video
                width="100%"
                height="auto"
                controls
                poster={`/api/videos/thumbnail/${videoId}`}
                className="video-player"
              >
                <source src={`/api/videos/stream/${videoId}`} type="video/mp4" />
                Your browser does not support the video tag.
              </video>
            </div>
            <div className="video-info">
              <h3 className="video-title">{video.title || `Video ${videoId}`}</h3>
              {video.description && <p className="video-description">{video.description}</p>}
            </div>
          </Link>
        );
      })}
    </div>
  );
}

export default App;
