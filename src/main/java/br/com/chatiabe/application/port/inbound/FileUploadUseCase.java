package br.com.chatiabe.application.port.inbound;

import java.util.UUID;

import br.com.chatiabe.application.dto.FileUploadResponse;

public interface FileUploadUseCase {

    FileUploadResponse uploadFile(String fileName, long fileSize, byte[] content);

    byte[] downloadFile(UUID fileId);
}
