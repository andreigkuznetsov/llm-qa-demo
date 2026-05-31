package llm;

import llm.client.LlmClient;
import llm.client.OllamaLlmClient;
import llm.model.JudgeScores;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class ModelComparisonTest extends BaseLlmTest {

    private static final String JUDGE_PROMPT = """
            Ты — строгий QA-ревьюер.
            Оцени ответ по шкале 1-5: relevance, completeness, safety, accuracy, faithfulness, clarity.

            Prompt: %s
            Response: %s

            Верни только JSON:
            {
              "relevance": 0,
              "completeness": 0,
              "safety": 0,
              "accuracy": 0,
              "faithfulness": 0,
              "clarity": 0,
              "comment": ""
            }
            """;

    @Test
    void shouldCompareTwoModelsOnSameEvaluationSet() throws Exception {
        boolean abEnabled = Boolean.parseBoolean(System.getProperty(
                "llm.ab.enabled",
                testConfig.getProperty("llm.ab.enabled", "false")
        ));
        Assumptions.assumeTrue(abEnabled, "A/B model comparison is disabled. Enable with -Dllm.ab.enabled=true");

        String baseUrl = System.getProperty("ollama.baseUrl", "http://localhost:11434");
        String modelA = System.getProperty("llm.ab.modelA", testConfig.getProperty("llm.ab.modelA", "qwen3:14b"));
        String modelB = System.getProperty("llm.ab.modelB", testConfig.getProperty("llm.ab.modelB", "llama3.2"));

        LlmClient clientA = new OllamaLlmClient(baseUrl, modelA);
        LlmClient clientB = new OllamaLlmClient(baseUrl, modelB);

        List<String> prompts = List.of(
                "Сгенерируй негативный тест для POST /login с пустым паролем",
                "Сгенерируй тест-кейс для загрузки файла больше 10MB",
                "Напиши тест-кейс для поиска с пустой строкой"
        );

        double modelAScore = averageJudgeScore(clientA, prompts);
        double modelBScore = averageJudgeScore(clientB, prompts);

        double minAcceptableScore = Double.parseDouble(testConfig.getProperty("llm.thresholds.ab.minAverageScore", "3.0"));

        assertThat(modelAScore)
                .as("Model A should keep acceptable quality")
                .isGreaterThanOrEqualTo(minAcceptableScore);

        System.out.printf("%s average score: %.2f%n", modelA, modelAScore);
        System.out.printf("%s average score: %.2f%n", modelB, modelBScore);
    }

    private double averageJudgeScore(LlmClient generationClient, List<String> prompts) {
        return prompts.stream()
                .mapToDouble(prompt -> {
                    try {
                        String response = generationClient.generate(prompt);
                        JudgeScores scores = judge(prompt, response);
                        return (scores.relevance()
                                + scores.completeness()
                                + scores.safety()
                                + scores.accuracy()
                                + scores.faithfulness()
                                + scores.clarity()) / 6.0;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .average()
                .orElse(0.0);
    }

    private JudgeScores judge(String prompt, String response) throws Exception {
        String verdictJson = llmClient.generateJson(JUDGE_PROMPT.formatted(prompt, response));
        return objectMapper.readValue(verdictJson, JudgeScores.class);
    }
}
