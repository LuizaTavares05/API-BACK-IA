import { useState, useEffect, useCallback } from 'react';
import { ChatSidebar } from './ChatSidebar';
import { ChatWindow } from './ChatWindow';
import { LoginForm } from './LoginForm';
import { RegisterForm } from './RegisterForm';
import { useAuth } from '../hooks/useAuth';
import { useSessions } from '../hooks/useSessions';
import { useMessages } from '../hooks/useMessages';
import { useDocuments } from '../hooks/useDocuments';
import { useRagQuery } from '../hooks/useRagQuery';

export function ChatLayout() {
  const { user, login, register, logout } = useAuth();
  const {
    sessions,
    fetchSessions,
    createSession,
  } = useSessions();
  const { messages, fetchMessages, clearMessages } = useMessages();
  const { uploadDocument } = useDocuments();
  const { status, response, sendQuery } = useRagQuery();

  const [activeSessionId, setActiveSessionId] = useState<string | undefined>();
  const [authView, setAuthView] = useState<'login' | 'register'>('login');
  const [combinedMessages, setCombinedMessages] = useState<typeof messages>([]);

  useEffect(() => {
    if (user) {
      fetchSessions();
    }
  }, [user, fetchSessions]);

  useEffect(() => {
    if (activeSessionId) {
      fetchMessages(activeSessionId);
      clearMessages();
    } else {
      clearMessages();
    }
  }, [activeSessionId, fetchMessages, clearMessages]);

  useEffect(() => {
    setCombinedMessages(messages);
  }, [messages]);

  useEffect(() => {
    if (response && !combinedMessages.find((m) => m.id === response.id)) {
      setCombinedMessages((prev) => [
        ...prev,
        {
          id: response.id,
          chatSessionId: response.chatSessionId,
          role: response.role,
          content: response.content,
          timestamp: response.timestamp,
        },
      ]);
    }
  }, [response, combinedMessages]);

  const handleSessionClick = useCallback((id: string) => {
    setActiveSessionId(id);
  }, []);

  const handleCreateSession = useCallback(
    async (title: string) => {
      const session = await createSession(title);
      setActiveSessionId(session.id);
    },
    [createSession]
  );

  const handleSendMessage = useCallback(
    async (content: string) => {
      if (!activeSessionId) {
        const session = await createSession(content.slice(0, 50));
        setActiveSessionId(session.id);
        return sendQuery(session.id, content);
      }
      return sendQuery(activeSessionId, content);
    },
    [activeSessionId, createSession, sendQuery]
  );

  const handleUploadDocument = useCallback(
    async (file: File) => {
      await uploadDocument(file);
    },
    [uploadDocument]
  );

  if (!user) {
    return (
      <div className="auth-container">
        {authView === 'login' ? (
          <LoginForm
            onLogin={login}
            onSwitchToRegister={() => setAuthView('register')}
          />
        ) : (
          <RegisterForm
            onRegister={register}
            onSwitchToLogin={() => setAuthView('login')}
          />
        )}
      </div>
    );
  }

  return (
    <div className="chat-layout">
      <ChatSidebar
        sessions={sessions}
        activeSessionId={activeSessionId}
        onSessionClick={handleSessionClick}
        onCreateSession={handleCreateSession}
        onUploadDocument={handleUploadDocument}
        onLogout={logout}
      />
      <ChatWindow
        messages={combinedMessages}
        onSendMessage={handleSendMessage}
        loading={status === 'loading'}
      />
    </div>
  );
}
