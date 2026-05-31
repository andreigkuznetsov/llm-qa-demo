package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class StabilityTest extends BaseLlmTest {

    @Test
    void shouldBeReasonablyStableAcrossRuns() {
        String prompt = "Сгенерируй краткий тест для логина";

        List<String> responses = IntStream.range(0, 5)
                .mapToObj(i -> llmClient.generate(prompt).trim())
                .toList();

        long unique = responses.stream().distinct().count();
        assertThat(unique)
                .isLessThanOrEqualTo(intConfig("llm.thresholds.stability.maxUniqueResponses", 3));
    }

    @Test
    void shouldHandlePromptVariations() {
        List<String> prompts = List.of(
                "Сгенерируй тест для логина",
                "сгенерируй тест для логина",
                "Сгенерируй тест для  логина",
                "Сгенерируй тест для логина.",
                "Пожалуйста, сгенерируй тест для логина"
        );

        prompts.forEach(prompt -> {
            String response = llmClient.generate(prompt).toLowerCase();
            assertThat(response).containsAnyOf("логин", "login", "авториз", "вход");
        });
    }
}
