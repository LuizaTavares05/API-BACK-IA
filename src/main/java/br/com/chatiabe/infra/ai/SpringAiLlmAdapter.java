package br.com.chatiabe.infra.ai;

import br.com.chatiabe.application.port.outbound.LlmProvider;
import br.com.chatiabe.domain.exception.LlmProviderException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class SpringAiLlmAdapter implements LlmProvider {

    private final ChatModel chatModel;

    public SpringAiLlmAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generate(String prompt) {
        try {
            return chatModel.call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getContent();
        } catch (Exception e) {
            throw new LlmProviderException("Failed to generate LLM response: " + e.getMessage(), e);
        }
    }
}
