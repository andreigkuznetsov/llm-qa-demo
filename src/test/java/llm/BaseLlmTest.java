package llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import llm.client.LlmClient;
import llm.client.OllamaLlmClient;
import org.junit.jupiter.api.BeforeAll;

import java.io.InputStream;
import java.util.Properties;

public abstract class BaseLlmTest {

    protected static LlmClient llmClient;
    protected static ObjectMapper objectMapper;
    protected static Properties testConfig;

    @BeforeAll
    static void init() throws Exception {
        String baseUrl = System.getProperty("ollama.baseUrl", "http://localhost:11434");
        String model = System.getProperty("ollama.model", "qwen3:8b");

        llmClient = new OllamaLlmClient(baseUrl, model);
        objectMapper = new ObjectMapper();

        testConfig = new Properties();
        try (InputStream in = BaseLlmTest.class.getClassLoader().getResourceAsStream("thresholds.properties")) {
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
