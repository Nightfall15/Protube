import './App.css';
import { BrowserRouter, Routes, Route, useNavigate, useParams } from 'react-router-dom';
import { useAllVideos } from './useAllVideos';
import React, { useState } from 'react';

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Header />
        <Routes>
          <Route path="/" element={<ContentApp />} />
          <Route path="/search/:query" element={<SearchResults />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

// ================= HEADER WITH SEARCH =================
function Header() {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/search/${encodeURIComponent(searchTerm.trim())}`);
      setSearchTerm('');
    }
  };

  return (
    <header className="App-header">
      <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
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
function VideoGrid({ videos }: { videos: any[] }) {
  return (
    <div className="video-grid">
      {videos.map((video, index) => {
        const videoId = video.id || index + 1;
        return (
          <div key={videoId + video.title} className="video-card">
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
          </div>
        );
      })}
    </div>
  );
}

export default App;
