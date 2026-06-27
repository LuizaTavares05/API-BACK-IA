package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.Document;

public interface WebhookNotifier {
    void notify(Document document);
}
