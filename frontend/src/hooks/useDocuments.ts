import { useState, useCallback, useRef } from 'react';
import { api } from '../services/api';
import type { DocumentStatusResponse } from '../types';

export function useDocuments() {
  const [uploading, setUploading] = useState(false);
  const [documentStatus, setDocumentStatus] = useState<Record<string, DocumentStatusResponse>>({});
  const [error, setError] = useState<string | null>(null);
  const pollingRef = useRef<Record<string, ReturnType<typeof setInterval>>>({});

  const stopPolling = useCallback((documentId: string) => {
    if (pollingRef.current[documentId]) {
      clearInterval(pollingRef.current[documentId]);
      delete pollingRef.current[documentId];
    }
  }, []);

  const uploadDocument = useCallback(async (file: File) => {
    setUploading(true);
    setError(null);
    try {
      const response = await api.uploadDocument(file);
      setDocumentStatus((prev) => ({
        ...prev,
        [response.id]: { ...response, status: 'PROCESSING' },
      }));

      pollingRef.current[response.id] = setInterval(async () => {
        try {
          const status = await api.getDocumentStatus(response.id);
          setDocumentStatus((prev) => ({ ...prev, [response.id]: status }));
          if (status.status === 'COMPLETED' || status.status === 'FAILED') {
            stopPolling(response.id);
          }
        } catch {
          stopPolling(response.id);
        }
      }, 2000);

      return response;
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : 'Upload failed';
      setError(message);
      throw e;
    } finally {
      setUploading(false);
    }
  }, [stopPolling]);

  const clearDocuments = useCallback(() => {
    Object.keys(pollingRef.current).forEach(stopPolling);
    setDocumentStatus({});
  }, [stopPolling]);

  return { uploading, documentStatus, error, uploadDocument, clearDocuments };
}
