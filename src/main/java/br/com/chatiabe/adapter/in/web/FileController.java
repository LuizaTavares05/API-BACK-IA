package br.com.chatiabe.adapter.in.web;

import br.com.chatiabe.application.dto.FileUploadResponse;
import br.com.chatiabe.application.port.inbound.FileUploadUseCase;
import br.com.chatiabe.infra.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/chat/files")
public class FileController {

    private final FileUploadUseCase fileUploadUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public FileController(FileUploadUseCase fileUploadUseCase, JwtTokenProvider jwtTokenProvider) {
        this.fileUploadUseCase = fileUploadUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<FileUploadResponse> uploadFile(HttpServletRequest request,
                                                         @RequestParam("file") MultipartFile file) {
        UUID userId = extractUserId(request);
        return ResponseEntity.ok(fileUploadUseCase.uploadFile(file, userId));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request,
                                               @PathVariable UUID fileId) {
        UUID userId = extractUserId(request);
        byte[] data = fileUploadUseCase.downloadFile(fileId, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    private UUID extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
