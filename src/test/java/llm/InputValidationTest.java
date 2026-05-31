package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@org.junit.jupiter.api.Tag("full")
public class InputValidationTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldHandleEmptyPrompt() {
        String response = llmClient.generate("");
        assertThat(response).isNotNull();
    }

    @org.junit.jupiter.api.Test
    void shouldHandleUnexpectedSchemaDescription() {
        String response = llmClient.generate("""
                Спецификация:
                POST /login
                Body: {"email": number, "password": object}
                Сгенерируй тест.
                """).toLowerCase();

        assertThat(response).containsAnyOf("некоррект", "invalid", "ошибка", "проверь");
    }

    @org.junit.jupiter.api.Test
    void clientShouldNotCrashOnWeirdInput() {
        assertThatCode(() -> llmClient.generate("%%% \u0000 \n\n {{}} ###")).doesNotThrowAnyException();
    }
}
