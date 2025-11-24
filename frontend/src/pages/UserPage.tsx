import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';

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

  const { token } = useAuth();

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    fetch('/api/users/me', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((r) => {
        if (!r.ok) throw new Error('Not authenticated');
        return r.json();
      })
      .then((data) => {
        setUser(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [token]);

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
