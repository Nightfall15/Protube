import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { getEnv } from '../utils/Env';
import { useAuth } from '../context/AuthContext';

type Comment = {
  id: number;
  author: string;
  text: string;
  createdAt?: string | null;
  replies: Comment[];
};

type Video = {
  id: number;
  title?: string;
  description?: string;
  videoUrl?: string;
  thumbnailUrl?: string;
  uploader?: string;
  likes?: number;
};

export default function VideoPage() {
  const { id } = useParams<{ id: string }>();
  const { token, user } = useAuth();

  const [video, setVideo] = useState<Video | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [hasLiked, setHasLiked] = useState(false);

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

    if (user && user.likedVideoIds) {
      setHasLiked(user.likedVideoIds.includes(Number(id)));
    }
  }, [id, user]);

  //Post new comment
  const submitComment = () => {
    //Ensure valid comment
    if (!newComment.trim() || !id || !user) return;

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

  const toggleLike = () => {
    if (!video || !token) return;

    const endpoint = hasLiked ? 'unlike' : 'like';

    axios
      .post(
        `${getEnv().API_BASE_URL}/videos/${id}/${endpoint}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
      .then((res) => {
        setVideo({ ...video, likes: res.data });
        setHasLiked(!hasLiked);
        if (user) {
          const updated = {
            ...user,
            likedVideoIds: hasLiked
              ? user.likedVideoIds.filter((v) => v !== Number(id))
              : [...user.likedVideoIds, Number(id)],
          };
          localStorage.setItem('user', JSON.stringify(updated));
        }
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
          {/* Video */}
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
            {user ? (
              <>
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Write a comment..."
                  style={{ width: '100%', minHeight: 80, marginTop: '0.5rem' }}
                />

                <button onClick={submitComment} style={{ marginTop: '0rem', padding: '0.5rem 1rem' }}>
                  Post Comment
                </button>
              </>
            ) : (
              <p style={{ color: '#777', marginTop: '0.5rem' }}>You must be logged in to post comments.</p>
            )}

            {comments.length === 0 && <p>No comments yet.</p>}

            <ul style={{ marginTop: '1rem', paddingLeft: 0, listStyle: 'none' }}>
              {comments.map((c) => (
                <CommentItem key={c.id} comment={c} onReply={postReply} />
              ))}
            </ul>
          </section>
        </>
      )}
    </div>
  );
}
