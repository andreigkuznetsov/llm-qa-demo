package llm.util;

import java.util.List;
import java.util.Set;

public final class MetricsUtils {

    private MetricsUtils() {
    }

    public static double precisionAtK(List<String> ranked, Set<String> relevant, int k) {
        long hitCount = ranked.stream().limit(k).filter(relevant::contains).count();
        return k == 0 ? 0.0 : (double) hitCount / k;
    }

    public static double recallAtK(List<String> ranked, Set<String> relevant, int k) {
        if (relevant.isEmpty()) {
            return 1.0;
        }
        long hitCount = ranked.stream().limit(k).filter(relevant::contains).count();
        return (double) hitCount / relevant.size();
    }

    public static double f1(double precision, double recall) {
        if (precision + recall == 0) {
            return 0.0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    public static double reciprocalRank(List<String> ranked, Set<String> relevant) {
        for (int i = 0; i < ranked.size(); i++) {
            if (relevant.contains(ranked.get(i))) {
                return 1.0 / (i + 1);
            }
        }
        return 0.0;
    }

    public static double mrr(List<List<String>> rankings, List<Set<String>> relevants) {
        if (rankings.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (int i = 0; i < rankings.size(); i++) {
            sum += reciprocalRank(rankings.get(i), relevants.get(i));
        }
        return sum / rankings.size();
    }
}
