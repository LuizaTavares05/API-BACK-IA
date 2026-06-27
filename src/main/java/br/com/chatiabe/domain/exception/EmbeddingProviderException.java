package br.com.chatiabe.domain.exception;

public class EmbeddingProviderException extends DomainException {
    public EmbeddingProviderException(String message) {
        super(message);
    }

    public EmbeddingProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
