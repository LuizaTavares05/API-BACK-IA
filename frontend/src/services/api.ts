const API_BASE = '/api';

class ApiError extends Error {
  constructor(
    public status: number,
    message: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

async function request<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: `HTTP ${response.status}`,
    }));
    throw new ApiError(response.status, error.message || 'Request failed');
  }

  if (response.status === 204) return undefined as T;

  return response.json();
}

export const api = {
  // Auth
  login: (data: { username: string; password: string }) =>
    request<{ token: string; expiresIn: number }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  register: (data: { username: string; password: string; email?: string }) =>
    request<{ id: string; username: string; createdAt: string }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Sessions
  listSessions: () =>
    request<Array<{ id: string; title: string; createdAt: string }>>('/chat/sessions'),

  createSession: (title: string) =>
    request<{ id: string; title: string; createdAt: string }>('/chat/sessions', {
      method: 'POST',
      body: JSON.stringify({ title }),
    }),

  // Messages (RAG)
  sendMessage: (chatSessionId: string, content: string) =>
    request<{
      id: string;
      chatSessionId: string;
      role: string;
      content: string;
      timestamp: string;
      sources: Array<{
        documentId: string;
        documentName: string;
        chunkIndex: number;
        excerpt: string;
        score: number;
      }>;
    }>('/chat/messages', {
      method: 'POST',
      body: JSON.stringify({ chatSessionId, content }),
    }),

  getMessages: (sessionId: string) =>
    request<
      Array<{
        id: string;
        chatSessionId: string;
        role: string;
        content: string;
        timestamp: string;
      }>
    >(`/chat/sessions/${sessionId}/messages`),

  // Documents
  uploadDocument: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return request<{
      id: string;
      fileName: string;
      fileSize: number;
      status: string;
      chunkCount: number;
      createdAt: string;
    }>('/documents', {
      method: 'POST',
      body: formData,
    });
  },

  getDocumentStatus: (documentId: string) =>
    request<{
      id: string;
      fileName: string;
      fileSize: number;
      status: string;
      chunkCount: number;
      createdAt: string;
      completedAt?: string;
      errorMessage?: string;
    }>(`/documents/${documentId}`),
};
