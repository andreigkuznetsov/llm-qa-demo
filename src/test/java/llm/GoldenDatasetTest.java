package llm;

import org.junit.jupiter.api.Tag;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class GoldenDatasetTest extends BaseLlmTest {

    static List<GoldenCase> goldenCases() throws Exception {
        try (InputStream in = GoldenDatasetTest.class.getClassLoader().getResourceAsStream("golden-dataset.json")) {
            return new ObjectMapperHolder().mapper.readValue(in, new TypeReference<>() {});
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("goldenCases")
    void shouldPassGoldenCase(GoldenCase c) {
        String response = llmClient.generate(c.prompt()).toLowerCase();

        c.mustContain().forEach(word -> assertThat(response).contains(word));
        c.mustNotContain().forEach(word -> assertThat(response).doesNotContain(word));
    }

    public record GoldenCase(String id, String prompt, List<String> mustContain, List<String> mustNotContain) {
        @Override public String toString() { return id; }
    }

    private static class ObjectMapperHolder {
        private final ObjectMapper mapper = new ObjectMapper();
    }
}
