package br.com.chatiabe.application.port.outbound;

public interface LlmProvider {
    String generate(String prompt);
}
