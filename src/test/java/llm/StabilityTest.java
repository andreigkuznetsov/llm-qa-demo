package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class StabilityTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldBeReasonablyStableAcrossRuns() {
        String prompt = "Сгенерируй краткий тест для логина";

        java.util.List<String> responses = java.util.stream.IntStream.range(0, 5)
                .mapToObj(i -> llmClient.generate(prompt).trim())
                .toList();

        long unique = responses.stream().distinct().count();
        assertThat(unique)
                .isLessThanOrEqualTo(intConfig("llm.thresholds.stability.maxUniqueResponses", 3));
    }

    @org.junit.jupiter.api.Test
    void shouldHandlePromptVariations() {
        java.util.List<String> prompts = java.util.List.of(
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
