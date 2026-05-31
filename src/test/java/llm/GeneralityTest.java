package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class GeneralityTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldNotBeTooGeneric() {
        String response = llmClient.generate("Сгенерируй тест для логина").toLowerCase();

        assertThat(response)
                .doesNotContain("проверьте систему")
                .doesNotContain("убедитесь что всё работает");
    }
}
