package br.com.chatiabe.domain.exception;

public class FileSizeExceededException extends DomainException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}
