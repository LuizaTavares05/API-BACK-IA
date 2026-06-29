interface ChatSessionProps {
  id: string;
  title: string;
  active: boolean;
  onClick: (id: string) => void;
}

export function ChatSession({ id, title, active, onClick }: ChatSessionProps) {
  return (
    <div
      className={`chat-session ${active ? 'active' : ''}`}
      onClick={() => onClick(id)}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => e.key === 'Enter' && onClick(id)}
    >
      <span className="session-icon">💬</span>
      <span className="session-title">{title}</span>
    </div>
  );
}
