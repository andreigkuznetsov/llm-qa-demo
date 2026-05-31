package llm.client;

public interface LlmClient {
    String generate(String prompt);
    String generateJson(String prompt);
    String chat(String systemPrompt, String userPrompt);
    llm.client.LlmRawResponse rawGenerate(String prompt);
}
