package llm.client;

public record LlmRawResponse(
        int statusCode,
        long responseTimeMs,
        String body,
        String text
) {
}
