import { useRef, useState } from 'react';

interface DocumentUploadButtonProps {
  onUpload: (file: File) => Promise<void>;
}

export function DocumentUploadButton({ onUpload }: DocumentUploadButtonProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);

  const handleClick = () => {
    inputRef.current?.click();
  };

  const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    try {
      await onUpload(file);
    } finally {
      setUploading(false);
      if (inputRef.current) {
        inputRef.current.value = '';
      }
    }
  };

  return (
    <div className="document-upload">
      <input
        ref={inputRef}
        type="file"
        accept=".txt,.pdf"
        onChange={handleChange}
        style={{ display: 'none' }}
      />
      <button onClick={handleClick} disabled={uploading} className="upload-button">
        {uploading ? 'Enviando...' : 'Upload Documento'}
      </button>
      <span className="upload-hint">.txt ou .pdf (máx 5MB)</span>
    </div>
  );
}
