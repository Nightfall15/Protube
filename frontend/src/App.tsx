import './App.css';
import { useAllVideos } from './useAllVideos';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
        <h1 className="App-title">ProTube</h1>
      </header>
      <ContentApp />
    </div>
  );
}

function ContentApp() {
  const { videos, loading } = useAllVideos();
  console.log('Videos array:', videos);

  if (loading === 'loading' || loading === 'idle') {
    return <div className="loading-spinner">Loading...</div>;
  }

  if (loading === 'error') {
    return <div className="error-message">Error loading videos</div>;
  }

  return (
    <div className="video-grid">
      {videos.map((video, index) => {
        const videoId = video.id || index + 1;
        return (
          <div key={videoId} className="video-card">
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
