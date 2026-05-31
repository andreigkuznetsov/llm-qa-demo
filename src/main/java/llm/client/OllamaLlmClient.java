package llm.client;

import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OllamaLlmClient implements LlmClient {

    private static final String JSON_UTF_8 = "application/json; charset=UTF-8";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String baseUri;
    private final String model;

    public OllamaLlmClient(String baseUri, String model) {
        this.baseUri = baseUri;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {
        return rawGenerate(prompt).text();
    }

    @Override
    public String generateJson(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("format", "json");
        body.put("stream", false);
        body.put("think", false);
        body.put("options", Map.of(
                "temperature", 0.0,
                "num_predict", 512
        ));

        Response response = given()
                .baseUri(baseUri)
                .contentType(JSON_UTF_8)
                .body(body)
                .when()
                .post("/api/generate");

        return extractText(response, "response", "message.content", "thinking");
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);
        body.put("think", false);
        body.put("options", Map.of(
                "temperature", 0.0,
                "num_predict", 256
        ));
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        Response response = given()
                .baseUri(baseUri)
                .contentType(JSON_UTF_8)
                .body(body)
                .when()
                .post("/api/chat");

        return extractText(response, "message.content", "response", "thinking");
    }

    @Override
    public LlmRawResponse rawGenerate(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("think", false);
        body.put("options", Map.of(
                "temperature", 0.0,
                "num_predict", 256
        ));

        Response response = given()
                .baseUri(baseUri)
                .contentType(JSON_UTF_8)
                .body(body)
                .when()
                .post("/api/generate");

        return new LlmRawResponse(
                response.statusCode(),
                response.time(),
                response.asString(),
                extractText(response, "response", "message.content", "thinking")
        );
    }

    private String extractText(Response response, String... paths) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(response.asString());

            for (String path : paths) {
                JsonNode node = getNode(root, path);
                if (node != null && !node.isNull()) {
                    String text = node.asText();
                    if (text != null && !text.isBlank()) {
                        return text;
                    }
                }
            }
        } catch (Exception ignored) {
            return "";
        }

        return "";
    }

    private JsonNode getNode(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode current = root;

        for (String part : parts) {
            if (current == null || current.isNull()) {
                return null;
            }
            current = current.get(part);
        }

        return current;
    }
}