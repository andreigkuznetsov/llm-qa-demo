package llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class HallucinationTest extends llm.BaseLlmTest {

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {
            "Расскажи про API эндпоинт /api/v99/quantum-teleport",
            "Напиши тест для библиотеки pytest-quantum-ai версии 15.0"
    })
    void shouldNotConfidentlyDescribeNonExistingThings(String prompt) {
        String response = llmClient.generate(prompt).toLowerCase();

        assertThat(response).doesNotContain(
                "этот эндпоинт принимает",
                "версия 15.0 поддерживает",
                "документация гласит"
        );
    }
}
