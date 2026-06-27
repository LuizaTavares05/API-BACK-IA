package br.com.chatiabe.domain.exception;

public class LlmProviderException extends DomainException {
    public LlmProviderException(String message) {
        super(message);
    }

    public LlmProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
