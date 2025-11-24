import React, { useEffect, useState } from 'react';
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

  const { token } = useAuth();

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

    if (!file) return alert('Select a video first');
    if (!user) return alert('User not loaded yet');

    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('description', description);
    formData.append('uploader', user.username);

    const res = await fetch('/api/videos/upload', {
      method: 'POST',
      body: formData,
      credentials: 'include',
    });

    if (res.ok) {
      alert('Video uploaded!');
      window.location.href = '/user';
    } else {
      const text = await res.text();
      alert('Upload failed: ' + text);
    }
  };

  if (!token || !user) return <div>Loading user info...</div>;

  return (
    <div className="upload-page">
      <h1>Subir Video</h1>

      <form onSubmit={handleUpload} className="upload-form">
        <label>
          Archivo de Video:
          <input type="file" accept="video/mp4" onChange={(e) => setFile(e.target.files?.[0] || null)} />
        </label>

        <label>
          Título:
          <input value={title} onChange={(e) => setTitle(e.target.value)} />
        </label>

        <label>
          Descripción:
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>

        <button type="submit">Subir</button>
      </form>
    </div>
  );
}
