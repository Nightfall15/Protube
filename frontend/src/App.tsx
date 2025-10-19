import './App.css';
import { useAllVideos } from './useAllVideos';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
        <ContentApp />
      </header>
    </div>
  );
}

function ContentApp() {
  const { videos, loading } = useAllVideos();
  console.log('Videos array:', videos);

  if (loading === 'loading' || loading === 'idle') {
    return <div>Loading...</div>;
  }

  if (loading === 'error') {
    return <div>Error loading videos</div>;
  }

  return (
    <div>
      {videos.map((video, index) => {
        const videoId = video.id || index + 1;
        return (
          <div key={videoId}>
            <h3>{video.title || `Video ${videoId}`}</h3>
            {video.description && <p>{video.description}</p>}
            <video width="320" height="240" controls poster={`/api/videos/thumbnail/${videoId}`}>
              <source src={`/api/videos/stream/${videoId}`} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
          </div>
        );
      })}
    </div>
  );
}

export default App;
