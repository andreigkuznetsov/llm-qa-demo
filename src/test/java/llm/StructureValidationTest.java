package llm;

import org.junit.jupiter.api.Tag;
import llm.model.GeneratedTestCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class StructureValidationTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldGenerateValidTestCaseJson() throws Exception {
        String json = llmClient.generateJson("""
    Сгенерируй тест-кейс для POST /login.

    Верни только валидный JSON.
    Не добавляй markdown.
    Не добавляй пояснения.
    Не добавляй текст до или после JSON.
    Не добавляй think-блок.

    Структура ответа строго такая:
    {
      "name": "...",
      "steps": ["..."],
      "expected": "..."
    }
    """);

        llm.model.GeneratedTestCase testCase = objectMapper.readValue(json, llm.model.GeneratedTestCase.class);

        assertThat(testCase.name()).isNotBlank();
        assertThat(testCase.steps()).isNotNull().isNotEmpty();
        assertThat(testCase.expected()).isNotBlank();
    }

    @org.junit.jupiter.api.Test
    void shouldNotReturnFreeTextWhenJsonExpected() {
        String json = llmClient.generateJson("""
    Верни только валидный JSON.
    Не добавляй markdown, пояснения и think-блок.

    Структура:
    {
      "name": "...",
      "steps": ["..."]
    }
    """);
        assertThat(json.trim()).startsWith("{").endsWith("}");
    }
}
