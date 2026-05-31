package llm.metrics;

public record ClassificationMetrics(
        int truePositive,
        int falsePositive,
        int trueNegative,
        int falseNegative,
        double accuracy,
        double precision,
        double recall,
        double f1
) {
}
