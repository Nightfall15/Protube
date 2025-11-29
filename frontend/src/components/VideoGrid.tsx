import { Link } from 'react-router-dom';

export type VideoItem = {
  id?: number | string;
  title?: string;
  description?: string;
};

export default function VideoGrid({ videos }: { videos: VideoItem[] }) {
  return (
    <div className="video-grid">
      {videos.map((video, index) => {
        const videoId = video.id || index + 1;

        return (
          <Link
            to={`/video/${videoId}`}
            key={String(videoId)}
            className="video-card"
            style={{ textDecoration: 'none', color: 'inherit' }}
          >
            <div className="video-thumbnail">
              <img
                src={`/api/videos/thumbnail/${videoId}`}
                alt={video.title || `Video ${videoId}`}
                className="thumbnail-image"
                style={{ width: '100%', height: 'auto', borderRadius: '8px' }}
              />
            </div>
            <div className="video-info">
              <h3 className="video-title">{video.title || `Video ${videoId}`}</h3>
            </div>
          </Link>
        );
      })}
    </div>
  );
}
