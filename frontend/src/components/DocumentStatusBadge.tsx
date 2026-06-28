interface DocumentStatusBadgeProps {
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
}

export function DocumentStatusBadge({ status }: DocumentStatusBadgeProps) {
  const labels: Record<string, string> = {
    PENDING: 'Pendente',
    PROCESSING: 'Processando',
    COMPLETED: 'Concluído',
    FAILED: 'Falhou',
  };

  return <span className={`status-badge status-${status.toLowerCase()}`}>{labels[status]}</span>;
}
