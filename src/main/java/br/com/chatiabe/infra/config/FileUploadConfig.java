package br.com.chatiabe.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FileUploadConfig {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
