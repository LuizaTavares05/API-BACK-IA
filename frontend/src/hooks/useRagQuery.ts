import { useState, useCallback } from 'react';
import { api } from '../services/api';
import type { RagMessageResponse } from '../types';

type QueryStatus = 'idle' | 'loading' | 'success' | 'error';

export function useRagQuery() {
  const [status, setStatus] = useState<QueryStatus>('idle');
  const [response, setResponse] = useState<RagMessageResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const sendQuery = useCallback(
    async (chatSessionId: string, content: string) => {
      setStatus('loading');
      setError(null);
      try {
        const result = await api.sendMessage(chatSessionId, content);
        setResponse(result);
        setStatus('success');
        return result;
      } catch (e: unknown) {
        const message = e instanceof Error ? e.message : 'Query failed';
        setError(message);
        setStatus('error');
        throw e;
      }
    },
    []
  );

  const reset = useCallback(() => {
    setStatus('idle');
    setResponse(null);
    setError(null);
  }, []);

  return { status, response, error, sendQuery, reset };
}
