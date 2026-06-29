export interface RagMessageResponse {
  id: string;
  chatSessionId: string;
  role: 'USER' | 'ASSISTANT';
  content: string;
  timestamp: string;
  sources: Source[];
}

export interface Source {
  documentId: string;
  documentName: string;
  chunkIndex: number;
  excerpt: string;
  score: number;
}

export interface DocumentResponse {
  id: string;
  fileName: string;
  fileSize: number;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  chunkCount: number;
  createdAt: string;
}

export interface DocumentStatusResponse extends DocumentResponse {
  completedAt?: string;
  errorMessage?: string;
}

export interface SendMessageRequest {
  chatSessionId: string;
  content: string;
}

export interface MessageResponse {
  id: string;
  chatSessionId: string;
  role: 'USER' | 'ASSISTANT';
  content: string;
  timestamp: string;
}

export interface ChatSessionResponse {
  id: string;
  title: string;
  createdAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
}

export interface RegisterResponse {
  id: string;
  username: string;
  createdAt: string;
}

export interface CreateSessionRequest {
  title: string;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}
