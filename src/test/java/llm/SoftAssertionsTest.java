package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class SoftAssertionsTest extends BaseLlmTest {

    @Test
    void shouldReturnAdequateGreeting() {
        String response = llmClient.chat(
                "Ты QA-ассистент компании TestCorp.",
                "Привет!"
        );

        String lower = response.toLowerCase();
        assertThat(List.of("привет", "здравствуй", "рад").stream().anyMatch(lower::contains)).isTrue();
        assertThat(response.length()).isBetween(6, 500);
        assertThat(lower).doesNotContain("error", "exception", "traceback");
    }

    @Test
    void shouldStayRelevant() {
        String response = llmClient.chat(
                "Ты QA-ассистент. Отвечай только про тестирование.",
                "Какой рецепт борща?"
        ).toLowerCase();

        assertThat(List.of("не могу", "только про тестирование", "вне моей компетенции")
                .stream().anyMatch(response::contains)).isTrue();
    }
}
