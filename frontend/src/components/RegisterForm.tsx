import { useState } from 'react';

interface RegisterFormProps {
  onRegister: (username: string, password: string, email?: string) => Promise<void>;
  onSwitchToLogin: () => void;
  error?: string | null;
  loading?: boolean;
}

export function RegisterForm({ onRegister, onSwitchToLogin, error, loading }: RegisterFormProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onRegister(username, password, email || undefined);
  };

  return (
    <form onSubmit={handleSubmit} className="register-form">
      <h2>Cadastro</h2>
      {error && <div className="error-message">{error}</div>}
      <div className="form-group">
        <label htmlFor="reg-username">Usuário</label>
        <input
          id="reg-username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          minLength={3}
        />
      </div>
      <div className="form-group">
        <label htmlFor="reg-email">Email (opcional)</label>
        <input
          id="reg-email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>
      <div className="form-group">
        <label htmlFor="reg-password">Senha</label>
        <input
          id="reg-password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={6}
        />
      </div>
      <button type="submit" disabled={loading}>
        {loading ? 'Cadastrando...' : 'Cadastrar'}
      </button>
      <p className="switch-action">
        Já tem conta?{' '}
        <button type="button" onClick={onSwitchToLogin} className="link-button">
          Entrar
        </button>
      </p>
    </form>
  );
}
