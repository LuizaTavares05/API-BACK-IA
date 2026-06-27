CREATE TABLE file_attachments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    data BYTEA NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_file_attachments_user_id ON file_attachments(user_id);
