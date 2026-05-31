package llm.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class TextSimilarityUtils {

    private TextSimilarityUtils() {
    }

    public static double jaccardSimilarity(String left, String right) {
        Set<String> leftTokens = tokenize(left);
        Set<String> rightTokens = tokenize(right);

        if (leftTokens.isEmpty() && rightTokens.isEmpty()) {
            return 1.0;
        }

        long intersection = leftTokens.stream()
                .filter(rightTokens::contains)
                .count();

        long union = leftTokens.size() + rightTokens.size() - intersection;
        return union == 0 ? 0.0 : (double) intersection / union;
    }

    public static Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-zа-я0-9 ]", " ")
                        .split("\\s+"))
                .filter(token -> token.length() > 2)
                .collect(Collectors.toSet());
    }
}
