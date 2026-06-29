import { MessageSources } from './MessageSources';
import type { RagMessageResponse, MessageResponse } from '../types';

interface MessageBubbleProps {
  message: MessageResponse | RagMessageResponse;
}

export function MessageBubble({ message }: MessageBubbleProps) {
  const isUser = message.role === 'USER';
  const sources = 'sources' in message ? message.sources : undefined;

  return (
    <div className={`message-bubble ${isUser ? 'user' : 'assistant'}`}>
      <div className="message-role">{isUser ? 'Você' : 'Assistente'}</div>
      <div className="message-content">{message.content}</div>
      {sources && <MessageSources sources={sources} />}
      <div className="message-timestamp">
        {new Date(message.timestamp).toLocaleTimeString()}
      </div>
    </div>
  );
}
