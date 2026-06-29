import { useState, useCallback, useEffect } from 'react';
import { api } from '../services/api';

interface User {
  token: string;
  username: string;
}

export function useAuth() {
  const [user, setUser] = useState<User | null>(() => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    return token && username ? { token, username } : null;
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = useCallback(async (username: string, password: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.login({ username, password });
      localStorage.setItem('token', response.token);
      localStorage.setItem('username', username);
      setUser({ token: response.token, username });
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : 'Login failed';
      setError(message);
      throw e;
    } finally {
      setLoading(false);
    }
  }, []);

  const register = useCallback(
    async (username: string, password: string, email?: string) => {
      setLoading(true);
      setError(null);
      try {
        await api.register({ username, password, email });
      } catch (e: unknown) {
        const message = e instanceof Error ? e.message : 'Registration failed';
        setError(message);
        throw e;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUser(null);
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setUser(null);
    }
  }, []);

  return { user, loading, error, login, register, logout, isAuthenticated: !!user };
}
