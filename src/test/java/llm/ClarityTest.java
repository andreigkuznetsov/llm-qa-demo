package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class ClarityTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldBeReadableAndStructured() {
        String response = llmClient.generate("Напиши понятный тест-кейс для логина");
        assertThat(response.split("\\R").length).isGreaterThan(2);
        assertThat(response.length()).isLessThan(1500);
    }
}
