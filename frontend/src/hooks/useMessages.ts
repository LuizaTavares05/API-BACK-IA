import { useState, useCallback } from 'react';
import { api } from '../services/api';
import type { MessageResponse } from '../types';

export function useMessages() {
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMessages = useCallback(async (sessionId: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getMessages(sessionId);
      setMessages(data);
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : 'Failed to fetch messages';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  const clearMessages = useCallback(() => {
    setMessages([]);
  }, []);

  return { messages, loading, error, fetchMessages, clearMessages };
}
