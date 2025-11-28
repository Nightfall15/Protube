import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

type UserDTO = {
  id: number;
  username: string;
  email: string;
  name: string;
  surname: string;
  number: number;
};

export default function UploadVideoPage() {
  const [file, setFile] = useState<File | null>(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [user, setUser] = useState<UserDTO | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { token } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) return;
    fetch('/api/users/me', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((r) => {
        if (!r.ok) throw new Error('Not authenticated');
        return r.json();
      })
      .then((data) => setUser(data))
      .catch(() => setUser(null));
  }, [token]);

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    if (!file) {
      setError('Please select a video file');
      setLoading(false);
      return;
    }
    if (!user) {
      setError('User not loaded yet');
      setLoading(false);
      return;
    }

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', title);
      formData.append('description', description);
      formData.append('uploader', user.username);

      const res = await fetch('/api/videos/upload', {
        method: 'POST',
        body: formData,
      });

      if (res.ok) {
        navigate('/user');
      } else {
        const text = await res.text();
        setError('Upload failed: ' + text);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error uploading video');
    } finally {
      setLoading(false);
    }
  };

  if (!token || !user) return <div className="loading-spinner">Loading user info...</div>;

  return (
    <div className="upload-page">
      <style>{styles}</style>
      <div className="card" role="main" aria-labelledby="upload-title">
        <form onSubmit={handleUpload} aria-label="Upload form">
          <h1 id="upload-title" className="title">
            Upload your video
          </h1>

          {error && (
            <div className="error" role="alert">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="file">Video File</label>
            <input
              id="file"
              type="file"
              accept="video/mp4"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              required
              className="file-input"
            />
            {file && <div className="file-name">{file.name}</div>}
          </div>

          <div>
            <label htmlFor="title">Title</label>
            <input
              id="title"
              name="title"
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              placeholder="Enter video title"
            />
          </div>

          <div>
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
              placeholder="Enter video description"
              rows={4}
            />
          </div>

          <div className="actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Uploading...' : 'Upload Video'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/user')}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = `
  .upload-page {
    height: 100vh;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--auth-bg);
    padding: 140px 20px 40px 20px;
    font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
    color: var(--text-primary);
    position: relative;
  }
  .upload-page::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='2.5' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.15'/%3E%3C/svg%3E");
    opacity: 0.4;
    pointer-events: none;
  }
  .card {
    width: 100%;
    max-width: 560px;
    background: var(--bg-card);
    border-radius: 4px;
    box-shadow: none;
    padding: 28px;
    position: relative;
    transform: rotate(-0.5deg);
  }
  .title {
    font-size: 20px;
    font-weight: 700;
    margin: 0 0 4px 0;
    color: var(--text-primary);
    text-shadow: 2px 2px 0 rgba(0,0,0,0.3);
    text-align: center;
  }
  .subtitle {
    margin: 0 0 16px 0;
    color: var(--text-secondary);
    line-height: 1.4;
    font-size: 14px;
    text-align: center;
  }
  form {
    display: grid;
    gap: 12px;
    padding: 10px 0;
  }
  label {
    font-size: 13px;
    color: var(--label-color);
    margin-bottom: 6px;
    display: block;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  input, textarea {
    width: 100%;
    padding: 12px 14px;
    border-radius: 0px;
    border: none;
    background: var(--input-bg);
    color: var(--text-primary);
    outline: none;
    box-shadow: none;
    transition: transform .05s;
    border-bottom: 1px dashed rgba(255,255,255,0.04);
    font-family: inherit;
  }
  input::placeholder, textarea::placeholder {
    color: var(--placeholder-color);
  }
  input:focus, textarea:focus {
    box-shadow: 0 0 0 3px rgba(96,165,250,0.06), inset 1px 1px 0 rgba(255,255,255,0.02);
    border-bottom-color: var(--accent);
  }
  textarea {
    resize: vertical;
    min-height: 80px;
  }
  .file-input {
    cursor: pointer;
  }
  .file-name {
    margin-top: 6px;
    padding: 8px 12px;
    background: rgba(96,165,250,0.08);
    border-radius: 2px;
    font-size: 13px;
    color: var(--accent);
    font-weight: 500;
  }
  .error {
    color: #ffd7d9;
    background: rgba(255, 40, 84, 0.12);
    padding: 10px 12px;
    border-radius: 2px;
    border: none;
    font-size: 14px;
    font-weight: 600;
  }
  .actions {
    display: flex;
    gap: 12px;
    align-items: center;
    margin-top: 8px;
  }
  .btn {
    flex: 1;
    padding: 12px 14px;
    border-radius: 2px;
    border: none;
    cursor: pointer;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    transition: transform .1s, box-shadow .08s;
  }
  .btn:active {
    transform: translate(2px, 2px);
  }
  .btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  .btn-primary {
    background: linear-gradient(90deg,#60a5fa,#7c3aed);
    color: white;
    box-shadow: none;
  }
  .btn-secondary {
    background: rgba(255,255,255,0.08);
    color: var(--text-primary);
    box-shadow: none;
  }
  .btn-secondary:hover {
    background: rgba(255,255,255,0.12);
  }
  .loading-spinner {
    text-align: center;
    padding: 40px;
    font-size: 1.5rem;
    color: var(--text-primary);
  }

  @media (max-width: 720px) {
    .upload-page {
      padding: 120px 20px 40px 20px;
      overflow: hidden;
    }
    .card {
      transform: none;
      max-width: 560px;
    }
  }
`;
