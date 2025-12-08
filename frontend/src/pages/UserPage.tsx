import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

type UserDTO = {
  id: number;
  username: string;
  email: string;
  name: string;
  surname: string;
  number: number | null;
};

export default function UserPage() {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);

  const { token } = useAuth();
  const navigate = useNavigate();

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
      <style>{styles}</style>
      <div className="card" role="main" aria-labelledby="user-title">
        <div className="hero" aria-hidden="false">
          <div style={{ display: 'flex', gap: 14, alignItems: 'center', justifyContent: 'center', height: '100%' }}>
            <img
              src="/protube-logo-removebg-preview.png"
              alt="Protube logo"
              style={{
                width: '220px',
                height: 'auto',
                objectFit: 'contain',
              }}
              aria-hidden="true"
            />
          </div>
        </div>

        <div className="content" aria-label="User profile">
          <div className="section">
            <h1 id="user-title" className="title">
              Perfil de {user.username}
            </h1>
            <p className="subtitle">Manage your profile and uploads</p>
          </div>

          <div className="section">
            <h2 className="section-title">Profile Information</h2>
            <div className="info-grid">
              <div className="info-item">
                <label>Full name</label>
                <div className="info-value">
                  {user.name} {user.surname}
                </div>
              </div>

              <div className="info-item">
                <label>Email</label>
                <div className="info-value">{user.email}</div>
              </div>

              <div className="info-item">
                <label>Username</label>
                <div className="info-value">{user.username}</div>
              </div>

              <div className="info-item">
                <label>Phone</label>
                <div className="info-value">{user.number ?? 'Not provided'}</div>
              </div>
            </div>

            <div className="actions">
              <button className="btn btn-primary" onClick={() => navigate('/user/upload')}>
                Subir nuevo video
              </button>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  );
}

const styles = `
  .user-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: flex-start; /* move everything to the left */
    background: var(--auth-bg);
    padding: 140px 20px 40px 60px; /* match Register padding but with left bias */
    font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
    color: var(--text-primary);
    position: relative;
  }
  .user-page::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='2.5' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.15'/%3E%3C/svg%3E");
    opacity: 0.4;
    pointer-events: none;
  }
  .card {
    width: 100%;
    max-width: 920px;
    background: var(--bg-card);
    border-radius: 4px;
    box-shadow: none;
    padding: 28px;
    display: grid;
    grid-template-columns: 0.45fr 1fr; /* hero column left, content right inside card */
    gap: 24px;
    position: relative;
    transform: none;
    margin-left: 12px; /* nudge away from the left edge */
  }
  .hero {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 12px;
    padding: 10px 20px;
  }
  .logo {
    width: 72px;
    height: 72px;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    background: transparent;
    color: var(--text-primary);
    box-shadow: 4px 4px 0 rgba(0,0,0,0.4);
  }
  .title {
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }
  .subtitle {
    margin: 0;
    color: var(--text-secondary);
    line-height: 1.4;
  }
  .content {
    display: flex;
    flex-direction: column;
    gap: 24px;
    padding: 10px 0;
    align-items: flex-start; /* align everything to the left inside the content column */
    text-align: left;
  }
  .section {
    display: flex;
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  .section-title {
    font-size: 13px;
    color: var(--label-color);
    margin: 0 0 8px 0;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  .info-grid {
    display: grid;
    gap: 12px;
  }
  .info-item {
    display: flex;
    flex-direction: column;
    gap: 6px;
    align-items: flex-start; /* left align each label/value pair */
  }
  label {
    font-size: 13px;
    color: var(--label-color);
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    text-align: left;
  }
  .info-value {
    padding: 0; /* remove boxed padding */
    border-radius: 0px;
    background: transparent; /* remove background box */
    color: var(--text-primary);
    border-bottom: none; /* remove underline */
    font-size: 15px;
    opacity: 0.95;
    white-space: nowrap;
  }
  .actions {
    display: flex;
    gap: 12px;
    margin-top: 8px;
    justify-content: flex-start; /* keep button on the left */
  }
  .btn {
    flex: 0 0 auto;
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
  .btn-primary {
    background: linear-gradient(90deg,#60a5fa,#7c3aed);
    color: white;
    box-shadow: none;
  }
  .loading-spinner, .error-message {
    text-align: center;
    padding: 20px;
    color: var(--text-primary);
  }

  /* Responsive: on narrow screens keep layout centered and stacked */
  @media (max-width: 720px) {
    .user-page { justify-content: center; padding: 120px 20px 40px 20px; } /* match responsive padding */
    .card { grid-template-columns: 1fr; transform: none; max-width: 560px; }
    .content, .section, .info-item, label { align-items: flex-start; text-align: left; }
    .actions { justify-content: center; }
  }
`;
