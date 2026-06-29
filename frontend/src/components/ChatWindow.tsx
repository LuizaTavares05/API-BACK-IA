import { useState, useRef, useEffect } from 'react';
import { MessageBubble } from './MessageBubble';
import type { MessageResponse, RagMessageResponse } from '../types';

interface ChatWindowProps {
  messages: MessageResponse[];
  onSendMessage: (content: string) => Promise<RagMessageResponse | void>;
  loading?: boolean;
}

export function ChatWindow({ messages, onSendMessage, loading }: ChatWindowProps) {
  const [input, setInput] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || loading) return;
    const content = input.trim();
    setInput('');
    await onSendMessage(content);
  };

  return (
    <div className="chat-window">
      <div className="messages-area">
        {messages.map((msg) => (
          <MessageBubble key={msg.id} message={msg} />
        ))}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={handleSubmit} className="chat-input-form">
        <input
          type="text"
          placeholder="Digite sua pergunta..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          disabled={loading}
          maxLength={10000}
        />
        <button type="submit" disabled={loading || !input.trim()}>
          {loading ? '...' : 'Enviar'}
        </button>
      </form>
    </div>
  );
}
