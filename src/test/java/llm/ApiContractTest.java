package llm;

import llm.client.LlmRawResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiContractTest extends BaseLlmTest {

    @Tag("smoke")
    @Test
    void shouldReturn200AndResponseBody() {
        LlmRawResponse response = llmClient.rawGenerate("Ответь одним словом: OK");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotBlank();
    }

    @Tag("smoke")
    @Test
    void shouldRespondWithinSla() {
        LlmRawResponse response = llmClient.rawGenerate("Ответь одним словом: OK");

        int maxLatencyMs = intConfig("llm.latency.max.ms", 180000);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.responseTimeMs()).isLessThan(maxLatencyMs);
    }
}