package llm.model;

import java.util.List;

public record GeneratedTestCase(
        String name,
        List<String> steps,
        String expected
) {
}
