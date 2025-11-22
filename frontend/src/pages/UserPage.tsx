import React, { useEffect, useState } from 'react';

type UserDTO = {
  id: number;
  username: string;
  email: string;
  name: string;
  surname: string;
  number: number;
};

export default function UserPage() {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/users/me', {
      credentials: 'include',
    })
      .then((r) => r.json())
      .then((data) => {
        setUser(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading-spinner">Loading user...</div>;
  if (!user) return <div className="error-message">User not found</div>;

  return (
    <div className="user-page">
      <h1>Perfil de {user.username}</h1>
      <p>
        <strong>Nombre:</strong> {user.name} {user.surname}
      </p>
      <p>
        <strong>Email:</strong> {user.email}
      </p>

      <hr />

      <h2>Mis Vídeos</h2>

      <button className="upload-button" onClick={() => (window.location.href = '/user/upload')}>
        Subir nuevo video
      </button>
    </div>
  );
}
