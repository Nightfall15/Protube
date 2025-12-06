import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { getEnv } from '../utils/Env';

type Comment = {
  id: number;
  author: string;
  text: string;
  createdAt?: string | null;
};

type Video = {
  id: number;
  title?: string;
  description?: string;
  videoUrl?: string;
  thumbnailUrl?: string;
  uploader?: string;
};

export default function VideoPage() {
  const { id } = useParams<{ id: string }>();

  const [video, setVideo] = useState<Video | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');

  //Load video and comments
  useEffect(() => {
    if (!id) return;

    //Get video
    axios
      .get(`${getEnv().API_BASE_URL}/videos/${id}`)
      .then((res) => setVideo(res.data))
      .catch(() => setVideo(null));

    //Get comments
    axios
      .get(`${getEnv().API_BASE_URL}/videos/${id}/comments`)
      .then((res) => setComments(res.data || []))
      .catch(() => setComments([]));
  }, [id]);

  //Post new comment
  const submitComment = () => {
    //Ensure valid comment
    if (!newComment.trim() || !id) return;

    //Call post with comment info
    axios
      .post<Comment>(`${getEnv().API_BASE_URL}/videos/${id}/comments`, {
        author: 'Anonymous', //To be turned into The UserTM
        text: newComment.trim(),
      })
      // Post success, refresh comment list with new comment
      .then((res) => {
        setComments((prev) => [...prev, res.data as Comment]);
        setNewComment('');
      })
      //Catch any possible error
      .catch((err) => {
        console.error(err);
        alert('Failed to post comment.');
      });
  };

  if (!id) return <div>Missing video ID.</div>;

  //Function to format date in comments
  const formatDate = (raw?: string | null) => {
    if (!raw) return '';
    const d = new Date(raw);
    return isNaN(d.getTime()) ? raw : d.toLocaleString();
  };

  // Ensure poster URL has a stable type: string | undefined
  const posterUrl: string | undefined = video && video.thumbnailUrl ? String(video.thumbnailUrl) : undefined;

  return (
    <div style={{ padding: '1.5rem' }}>
      {!video ? (
        <div>Loading video...</div>
      ) : (
        <>
          <h1>{video.title}</h1>

          {posterUrl ? (
            <video
              controls
              style={{ width: '100%', maxWidth: 900, marginTop: '1rem' }}
              src={`${getEnv().API_BASE_URL}/videos/stream/${id}`}
              poster={posterUrl}
            />
          ) : (
            <video
              controls
              style={{ width: '100%', maxWidth: 900, marginTop: '1rem' }}
              src={`${getEnv().API_BASE_URL}/videos/stream/${id}`}
            />
          )}

          {/* Description box: fixed max height with vertical scrollbar when overflowing */}
          <div
            style={{
              marginTop: '1rem',
              width: '100%',
              maxWidth: 900,
              border: '1px solid #ddd',
              borderRadius: 6,
              padding: '1rem',
              background: '#fafafa',
              color: '#000',
              maxHeight: 200,
              overflowY: 'auto',
              boxSizing: 'border-box',
              whiteSpace: 'pre-wrap',
            }}
          >
            {video.description}
          </div>

          {/*Comments*/}
          <section style={{ marginTop: '2rem' }}>
            <h2>Comments</h2>

            {/* Comment box */}
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Write a comment..."
              style={{ width: '100%', minHeight: 80, marginTop: '0.5rem' }}
            />

            <button onClick={submitComment} style={{ marginTop: '0rem', padding: '0.5rem 1rem' }}>
              Post Comment
            </button>

            {comments.length === 0 && <p>No comments yet. Pathetic.</p>}

            <ul style={{ marginTop: '1rem', paddingLeft: 0, listStyle: 'none' }}>
              {comments.map((c) => (
                <li
                  key={c.id}
                  style={{
                    padding: '0.6rem 0',
                    borderBottom: '1px solid #ddd',
                  }}
                >
                  <strong>{c.author}</strong>
                  <div>{c.text}</div>
                  <small style={{ color: '#555' }}>{formatDate(c.createdAt)}</small>
                </li>
              ))}
            </ul>
          </section>
        </>
      )}
    </div>
  );
}
