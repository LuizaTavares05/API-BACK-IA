package br.com.chatiabe.adapter.out.webhook;

import br.com.chatiabe.application.port.outbound.WebhookNotifier;
import br.com.chatiabe.domain.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class N8nWebhookNotifier implements WebhookNotifier {

    private static final Logger log = LoggerFactory.getLogger(N8nWebhookNotifier.class);

    private final RestClient restClient;

    @Value("${app.n8n.webhook-url:}")
    private String webhookUrl;

    public N8nWebhookNotifier() {
        this.restClient = RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Webhook request to: {}", request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    @Override
    @Async("webhookExecutor")
    public void notify(Document document) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("Webhook URL not configured, skipping n8n notification for document {}", document.getId());
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "event", "document.indexed",
                    "timestamp", LocalDateTime.now().toString(),
                    "data", Map.of(
                            "documentId", document.getId(),
                            "fileName", document.getFileName(),
                            "fileSize", document.getFileSize(),
                            "chunkCount", document.getChunkCount(),
                            "status", document.getStatus(),
                            "userId", document.getUserId(),
                            "indexedAt", document.getCompletedAt() != null
                                    ? document.getCompletedAt().toString()
                                    : LocalDateTime.now().toString()
                    )
            );

            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Webhook notification sent successfully for document {}", document.getId());
        } catch (Exception e) {
            log.warn("Failed to send webhook notification for document {}: {}", document.getId(), e.getMessage());
        }
    }
}
