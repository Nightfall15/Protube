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
  }, [id]);

  //Post new comment
  const submitComment = () => {
    //Ensure valid comment
    if (!newComment.trim() || !id || !user) return;

    //Call post with comment info
    axios
      .post(`${getEnv().API_BASE_URL}/videos/${id}/comments`, null, {
        params: {
          author: user.username,
          text: newComment.trim(),
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      //Post success, refresh comment list with new comment
      .then((res) => {
        setComments([...comments, res.data]);
        setNewComment('');
      })
      //Catch any possible error
      .catch((err) => {
        console.error(err);
        alert('Failed to post comment.');
      });
  };

  const toggleLike = () => {
    if (!id || !video || !token) return;

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
      })
      .catch(() => alert('Failed to update like.'));
  };

  if (!id) return <div>Missing video ID.</div>;

  //Function to format date in comments
  const formatDate = (raw?: string | null) => {
    if (!raw) return '';
    const d = new Date(raw);
    return isNaN(d.getTime()) ? raw : d.toLocaleString();
  };

  function CommentItem({ comment, onReply }: { comment: Comment; onReply: (parentId: number, text: string) => void }) {
    const { user } = useAuth();
    const [replying, setReplying] = useState(false);
    const [replyText, setReplyText] = useState('');

    const sendReply = () => {
      if (!replyText.trim()) return;
      onReply(comment.id, replyText);
      setReplyText('');
      setReplying(false);
    };

    //html
    return (
      <li style={{ padding: '0.6rem 0', borderBottom: '1px solid #ddd' }}>
        <strong>{comment.author}</strong>
        <div>{comment.text}</div>
        <small style={{ color: '#555' }}>{formatDate(comment.createdAt)}</small>

        {/* reply button */}
        <div>{user && <button onClick={() => setReplying(!replying)}>Reply</button>}</div>

        {replying && (
          <div style={{ marginTop: '0.5rem' }}>
            <textarea
              value={replyText}
              onChange={(e) => setReplyText(e.target.value)}
              placeholder="Write a reply..."
              style={{ width: '100%', minHeight: 60 }}
            />
            <button onClick={sendReply}>Send Reply</button>
          </div>
        )}

        {/* render child replies recursively */}
        {comment.replies?.length > 0 && (
          <ul style={{ listStyle: 'none', paddingLeft: '1.5rem', borderLeft: '2px solid #ccc' }}>
            {comment.replies.map((r) => (
              <CommentItem key={r.id} comment={r} onReply={onReply} />
            ))}
          </ul>
        )}
      </li>
    );
  }

  const postReply = (parentId: number, text: string) => {
    axios
      .post(`${getEnv().API_BASE_URL}/videos/${id}/comments`, null, {
        params: {
          author: user.username,
          text,
          parentId,
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        // reload full comment tree
        return axios.get(`${getEnv().API_BASE_URL}/videos/${id}/comments`);
      })
      .then((res) => setComments(res.data));
  };

  return (
    <div style={{ padding: '1.5rem' }}>
      {!video ? (
        <div>Loading video...</div>
      ) : (
        <>
          {/* Video */}
          <h1>{video.title}</h1>

          <video
            controls
            style={{ width: '100%', maxWidth: 900, marginTop: '1rem' }}
            src={`${getEnv().API_BASE_URL}/videos/stream/${id}`}
            poster={video.thumbnailUrl}
          />

          {/* Like button */}
          <p>
            <button style={{ marginTop: '0.5rem', padding: '0.4rem 1rem' }} onClick={toggleLike} disabled={!token}>
              {hasLiked ? '❤ Liked!' : '❤ Like'} ({video.likes ?? 0})
            </button>
          </p>

          <p style={{ marginTop: '1rem' }}>{video.description}</p>

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
