import { useState, useEffect } from 'react';
import { ChatSession } from './ChatSession';
import { DocumentUploadButton } from './DocumentUploadButton';
import type { ChatSessionResponse } from '../types';

interface ChatSidebarProps {
  sessions: ChatSessionResponse[];
  activeSessionId?: string;
  onSessionClick: (id: string) => void;
  onCreateSession: (title: string) => Promise<void>;
  onUploadDocument: (file: File) => Promise<void>;
  onLogout: () => void;
}

export function ChatSidebar({
  sessions,
  activeSessionId,
  onSessionClick,
  onCreateSession,
  onUploadDocument,
  onLogout,
}: ChatSidebarProps) {
  const [newTitle, setNewTitle] = useState('');

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;
    await onCreateSession(newTitle.trim());
    setNewTitle('');
  };

  return (
    <aside className="chat-sidebar">
      <div className="sidebar-header">
        <h2>Chat IA</h2>
        <button onClick={onLogout} className="logout-button">
          Sair
        </button>
      </div>

      <form onSubmit={handleCreate} className="new-session-form">
        <input
          type="text"
          placeholder="Nova conversa..."
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
        />
        <button type="submit">+</button>
      </form>

      <DocumentUploadButton onUpload={onUploadDocument} />

      <div className="sessions-list">
        {sessions.map((s) => (
          <ChatSession
            key={s.id}
            id={s.id}
            title={s.title}
            active={s.id === activeSessionId}
            onClick={onSessionClick}
          />
        ))}
      </div>
    </aside>
  );
}
