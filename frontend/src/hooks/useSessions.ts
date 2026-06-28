import { useState, useCallback } from 'react';
import { api } from '../services/api';
import type { ChatSessionResponse } from '../types';

export function useSessions() {
  const [sessions, setSessions] = useState<ChatSessionResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchSessions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.listSessions();
      setSessions(data);
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : 'Failed to fetch sessions';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  const createSession = useCallback(async (title: string) => {
    setError(null);
    try {
      const session = await api.createSession(title);
      setSessions((prev) => [session, ...prev]);
      return session;
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : 'Failed to create session';
      setError(message);
      throw e;
    }
  }, []);

  return { sessions, loading, error, fetchSessions, createSession };
}
