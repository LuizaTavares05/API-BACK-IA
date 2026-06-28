import type { Source } from '../types';

interface SourceCardProps {
  source: Source;
}

export function SourceCard({ source }: SourceCardProps) {
  return (
    <div className="source-card">
      <div className="source-header">
        <span className="source-document">{source.documentName}</span>
        <span className="source-score">{(source.score * 100).toFixed(0)}%</span>
      </div>
      <div className="source-chunk">Trecho #{source.chunkIndex}</div>
      <p className="source-excerpt">{source.excerpt}</p>
    </div>
  );
}
