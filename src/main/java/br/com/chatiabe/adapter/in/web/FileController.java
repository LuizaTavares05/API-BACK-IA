package br.com.chatiabe.adapter.in.web;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.chatiabe.application.dto.FileUploadResponse;
import br.com.chatiabe.application.port.inbound.FileUploadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/chat/files")
public class FileController {

    private final FileUploadUseCase fileUploadUseCase;

    public FileController(FileUploadUseCase fileUploadUseCase) {
        this.fileUploadUseCase = fileUploadUseCase;
    }

    @PostMapping
    @Operation(summary = "Fazer upload de arquivo anexo (TXT ou PDF, máx 5MB)")
    @ApiResponse(responseCode = "200", description = "Arquivo enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Formato ou tamanho inválido")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "413", description = "Arquivo excede o limite de 5MB")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            var response = fileUploadUseCase.uploadFile(
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getBytes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Baixar arquivo anexado")
    @ApiResponse(responseCode = "200", description = "Arquivo baixado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID fileId) {
        var content = fileUploadUseCase.downloadFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
