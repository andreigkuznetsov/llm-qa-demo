package llm.model;

import java.util.List;

public record GeneratedTestCase(
        String name,
        java.util.List<String> steps,
        String expected
) {
}
