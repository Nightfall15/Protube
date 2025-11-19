import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    name: '',
    number: '',
    password: '',
    surname: '',
    username: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        // set component error and return instead of throwing to avoid local throw warning
        setError(errorText || 'Error en el registro');
        setLoading(false);
        return;
      }

      await login(formData.username, formData.password);
      navigate('/');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al registrar usuario. Intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <style>{`
  .register-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--auth-bg);
    padding: 140px 20px 40px 20px; /* Add top padding to account for fixed header */
    font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
    color: var(--text-primary);
    position: relative;
  }
  .register-page::before {
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
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    position: relative;
    transform: rotate(-0.5deg);
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
    text-shadow: 2px 2px 0 rgba(0,0,0,0.3);
  }
  .subtitle {
    margin: 0;
    color: var(--text-secondary);
    line-height: 1.4;
  }
  form {
    display: grid;
    gap: 12px;
    padding: 10px 0;
  }
  .grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
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
  .full {
    grid-column: 1 / -1;
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
  .btn-primary {
    background: linear-gradient(90deg,#60a5fa,#7c3aed);
    color: white;
    box-shadow: none; /* remove shadow under create account */
  }
`}</style>
      <div className="card" role="main" aria-labelledby="register-title">
        <div className="hero" aria-hidden="false">
          <div style={{ display: 'flex', gap: 14, alignItems: 'center', justifyContent: 'center', height: '100%' }}>
            <img
              src="/protube-logo-removebg-preview.png"
              alt="Protube logo"
              style={{
                width: '260px',
                height: 'auto',
                objectFit: 'contain',
              }}
              aria-hidden="true"
            />
          </div>
        </div>

        <form onSubmit={handleSubmit} aria-label="Register form">
          {error && (
            <div className="error" role="alert">
              {error}
            </div>
          )}

          <div className="grid">
            <div>
              <label htmlFor="username">Username</label>
              <input
                id="username"
                name="username"
                type="text"
                value={formData.username}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label htmlFor="name">First name</label>
              <input id="name" name="name" type="text" value={formData.name} onChange={handleChange} required />
            </div>
            <div>
              <label htmlFor="surname">Last name</label>
              <input
                id="surname"
                name="surname"
                type="text"
                value={formData.surname}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label htmlFor="email">Email</label>
              <input id="email" name="email" type="email" value={formData.email} onChange={handleChange} required />
            </div>
            <div>
              <label htmlFor="number">Number</label>
              <input id="number" name="number" type="text" value={formData.number} onChange={handleChange} />
            </div>
          </div>

          <div className="actions full" style={{ marginTop: 6 }}>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Registering...' : 'Create account'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
