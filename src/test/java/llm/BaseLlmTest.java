package llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import llm.client.LlmClient;
import llm.client.OllamaLlmClient;
import org.junit.jupiter.api.BeforeAll;

import java.io.InputStream;
import java.util.Properties;

public abstract class BaseLlmTest {

    protected static llm.client.LlmClient llmClient;
    protected static com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    protected static java.util.Properties testConfig;

    @org.junit.jupiter.api.BeforeAll
    static void init() throws Exception {
        String baseUrl = System.getProperty("ollama.baseUrl", "http://localhost:11434");
        String model = System.getProperty("ollama.model", "qwen3:8b");

        llmClient = new llm.client.OllamaLlmClient(baseUrl, model);
        objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        testConfig = new java.util.Properties();
        try (java.io.InputStream in = llm.BaseLlmTest.class.getClassLoader().getResourceAsStream("thresholds.properties")) {
            if (in != null) {
                testConfig.load(in);
            }
        }
    }

    protected int intConfig(String key, int defaultValue) {
        String value = System.getProperty(key, testConfig.getProperty(key, String.valueOf(defaultValue)));
        return Integer.parseInt(value);
    }
}
