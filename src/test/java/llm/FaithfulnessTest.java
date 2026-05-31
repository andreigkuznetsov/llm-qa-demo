package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class FaithfulnessTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldUseOnlyProvidedContext() {
        String context = """
                API:
                POST /api/v2/auth/login
                statuses: 200, 401
                """;

        String response = llmClient.generate("""
                Используй только этот контекст:
                %s

                Сгенерируй тест.
                """.formatted(context));

        String lower = response.toLowerCase();
        assertThat(lower).contains("/api/v2/auth/login");
        assertThat(lower).doesNotContain("/register", "/admin", "500");
    }

    @org.junit.jupiter.api.Test
    void shouldRejectUnsupportedClaims() {
        String context = "Документ содержит только endpoint GET /users";
        String response = llmClient.generate("""
                Используй только контекст:
                %s
                Перечисли все endpoints.
                """.formatted(context)).toLowerCase();

        assertThat(response).contains("/users");
        assertThat(response).doesNotContain("/orders", "/login", "/delete");
    }
}
