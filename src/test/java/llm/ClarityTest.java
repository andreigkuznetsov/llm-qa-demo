package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class ClarityTest extends BaseLlmTest {

    @Test
    void shouldBeReadableAndStructured() {
        String response = llmClient.generate("Напиши понятный тест-кейс для логина");
        assertThat(response.split("\\R").length).isGreaterThan(2);
        assertThat(response.length()).isLessThan(1500);
    }
}
