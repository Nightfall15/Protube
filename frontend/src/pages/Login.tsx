// src/pages/Login.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/');
    } catch {
      setError('Usuario o contraseña incorrectos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <style>{`
  .login-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--auth-bg);
    padding: 140px 20px 40px 20px; /* account for fixed header */
    font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
    color: var(--text-primary);
    position: relative;
  }
  .login-page::before {
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
    border-radius: 2px;
    box-shadow: none; /* remove shadowing */
    padding: 28px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    position: relative;
    transform: rotate(-0.6deg); /* small tilt for "rough" feel */
    border: 1px solid rgba(255,255,255,0.02);
  }
  .hero {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 12px;
    padding: 10px 20px;
    align-items: center;
  }
  .logo {
    width: 260px;
    height: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    background: transparent;
    color: var(--text-primary);
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
  input {
    width: 100%;
    padding: 12px 14px;
    border-radius: 0px;
    border: none; /* remove borders from info boxes */
    background: var(--input-bg);
    color: var(--text-primary);
    outline: none;
    box-shadow: none; /* remove inset shadow */
    transition: transform .05s;
    border-bottom: 1px dashed rgba(255,255,255,0.04); /* subtle rough underline */
  }
  input::placeholder { color: var(--placeholder-color); }
  input:focus { box-shadow: 0 0 0 3px rgba(96,165,250,0.06), inset 1px 1px 0 rgba(255,255,255,0.02); border-bottom-color: var(--accent); }
  .full { grid-column: 1 / -1; }
  .error { color: #ffd7d9; background: rgba(255,40,84,0.06); padding: 10px 12px; border-radius: 2px; border: none; font-size: 14px; font-weight: 600; }
  .actions { display: flex; gap: 12px; align-items: center; margin-top: 8px; }
  .btn { flex: 1; padding: 12px 14px; border-radius: 0px; border: none; cursor: pointer; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; transition: transform .06s; }
  .btn-primary { background: linear-gradient(90deg,#60a5fa,#7c3aed); color: white; box-shadow: none; }
  @media (max-width: 768px) { .card { grid-template-columns: 1fr; padding: 20px; } }
`}</style>

      <div className="card" role="main" aria-labelledby="login-title">
        <div className="hero">
          <div style={{ display: 'flex', gap: 14, alignItems: 'center', justifyContent: 'center', height: '100%' }}>
            <img
              src="/protube-logo-removebg-preview.png"
              alt="Protube logo"
              className="logo"
              style={{ width: '260px', height: 'auto', objectFit: 'contain' }}
              aria-hidden="true"
            />
          </div>
        </div>

        <form onSubmit={handleSubmit} aria-label="Login form">
          <h2 id="login-title" style={{ margin: 0, color: 'var(--text-primary)' }}>
            Iniciar Sesión
          </h2>
          {error && (
            <div className="error" role="alert">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="username">Usuario</label>
            <input
              id="username"
              name="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              name="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="actions full" style={{ marginTop: 6 }}>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Ingresando...' : 'Ingresar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
