import { useState } from 'react';
import { SourceCard } from './SourceCard';
import type { Source } from '../types';

interface MessageSourcesProps {
  sources: Source[];
}

export function MessageSources({ sources }: MessageSourcesProps) {
  const [expanded, setExpanded] = useState(false);

  if (!sources || sources.length === 0) return null;

  return (
    <div className="message-sources">
      <button
        className="sources-toggle"
        onClick={() => setExpanded(!expanded)}
      >
        {expanded ? 'Ocultar' : 'Mostrar'} fontes ({sources.length})
      </button>
      {expanded && (
        <div className="sources-list">
          {sources.map((source, index) => (
            <SourceCard key={`${source.documentId}-${source.chunkIndex}`} source={source} />
          ))}
        </div>
      )}
    </div>
  );
}
