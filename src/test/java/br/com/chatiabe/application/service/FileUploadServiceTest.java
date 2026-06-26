package br.com.chatiabe.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.exception.FileSizeExceededException;
import br.com.chatiabe.domain.exception.UnsupportedFileFormatException;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService(fileStorageService);
    }

    @Test
    void shouldUploadTxtFile() {
        when(fileStorageService.save(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = fileUploadService.uploadFile("doc.txt", 1024L, "content".getBytes());

        assertNotNull(response.fileId());
        assertEquals("doc.txt", response.fileName());
        assertEquals(1024L, response.fileSize());
    }

    @Test
    void shouldUploadPdfFile() {
        when(fileStorageService.save(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = fileUploadService.uploadFile("doc.pdf", 2048L, "pdf-content".getBytes());

        assertNotNull(response.fileId());
        assertEquals("doc.pdf", response.fileName());
    }

    @Test
    void shouldRejectUnsupportedFormat() {
        assertThrows(UnsupportedFileFormatException.class,
                () -> fileUploadService.uploadFile("image.png", 1024L, "data".getBytes()));
    }

    @Test
    void shouldRejectFileTooLarge() {
        var oversized = 6_000_000L;
        assertThrows(FileSizeExceededException.class,
                () -> fileUploadService.uploadFile("doc.txt", oversized, "data".getBytes()));
    }

    @Test
    void shouldDownloadFile() {
        var fileId = UUID.randomUUID();
        var content = "file-content".getBytes();
        when(fileStorageService.getContent(fileId)).thenReturn(java.util.Optional.of(content));

        var result = fileUploadService.downloadFile(fileId);

        assertArrayEquals(content, result);
    }

    @Test
    void shouldThrowOnDownloadNonExistent() {
        var fileId = UUID.randomUUID();
        when(fileStorageService.getContent(fileId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> fileUploadService.downloadFile(fileId));
    }
}
