package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface FileUploadUseCase {
    FileUploadResponse uploadFile(MultipartFile file, UUID userId);
    byte[] downloadFile(UUID fileId, UUID userId);
}
