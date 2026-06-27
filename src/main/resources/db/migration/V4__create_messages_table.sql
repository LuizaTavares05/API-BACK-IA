CREATE TABLE messages (
    id UUID PRIMARY KEY,
    chat_session_id UUID NOT NULL REFERENCES chat_sessions(id),
    role VARCHAR(20) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_messages_chat_session_id ON messages(chat_session_id);
