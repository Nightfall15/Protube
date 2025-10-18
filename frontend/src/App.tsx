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
  // console.log('Loading:', loading);
  console.log('Videos array:', videos);
  // console.log('Videos length:', videos?.length);

  if (loading === 'loading' || loading === 'idle') {
    return <div>Loading...</div>;
  }

  if (loading === 'error') {
    return <div>Error loading videos</div>;
  }

  return (
    <div>
      {videos.map((video) => (
        <div key={video.id}>
          <h3>{video.title || `Video ${video.id}`}</h3>
          {video.description && <p>{video.description}</p>}
          <video width="320" height="240" controls poster={`/api/videos/thumbnail/${video.id}`}>
            <source src={`/api/videos/stream/${video.id}`} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
        </div>
      ))}
    </div>
  );
}

export default App;
