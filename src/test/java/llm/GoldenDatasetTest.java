package llm;

import org.junit.jupiter.api.Tag;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class GoldenDatasetTest extends llm.BaseLlmTest {

    static java.util.List<llm.GoldenDatasetTest.GoldenCase> goldenCases() throws Exception {
        try (java.io.InputStream in = llm.GoldenDatasetTest.class.getClassLoader().getResourceAsStream("golden-dataset.json")) {
            return new llm.GoldenDatasetTest.ObjectMapperHolder().mapper.readValue(in, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        }
    }

    @org.junit.jupiter.params.ParameterizedTest(name = "{0}")
    @org.junit.jupiter.params.provider.MethodSource("goldenCases")
    void shouldPassGoldenCase(llm.GoldenDatasetTest.GoldenCase c) {
        String response = llmClient.generate(c.prompt()).toLowerCase();

        c.mustContain().forEach(word -> assertThat(response).contains(word));
        c.mustNotContain().forEach(word -> assertThat(response).doesNotContain(word));
    }

    public record GoldenCase(String id, String prompt, java.util.List<String> mustContain, java.util.List<String> mustNotContain) {
        @Override public String toString() { return id; }
    }

    private static class ObjectMapperHolder {
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }
}
