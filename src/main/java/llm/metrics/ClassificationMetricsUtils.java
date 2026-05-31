package llm.metrics;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public final class ClassificationMetricsUtils {

    private ClassificationMetricsUtils() {
    }

    public static ClassificationMetrics calculate(List<Integer> actual, List<Integer> predicted) {
        validateSameSize(actual, predicted);

        int tp = 0;
        int fp = 0;
        int tn = 0;
        int fn = 0;

        for (int i = 0; i < actual.size(); i++) {
            int a = actual.get(i);
            int p = predicted.get(i);

            if (a == 1 && p == 1) {
                tp++;
            } else if (a == 0 && p == 1) {
                fp++;
            } else if (a == 0 && p == 0) {
                tn++;
            } else if (a == 1 && p == 0) {
                fn++;
            } else {
                throw new IllegalArgumentException("Only binary labels 0/1 are supported");
            }
        }

        double accuracy = safeDivide(tp + tn, actual.size());
        double precision = safeDivide(tp, tp + fp);
        double recall = safeDivide(tp, tp + fn);
        double f1 = precision + recall == 0.0 ? 0.0 : 2 * precision * recall / (precision + recall);

        return new ClassificationMetrics(tp, fp, tn, fn, accuracy, precision, recall, f1);
    }

    public static double aucRoc(List<Integer> actual, List<Double> probabilities) {
        validateSameSize(actual, probabilities);

        long positives = actual.stream().filter(label -> label == 1).count();
        long negatives = actual.stream().filter(label -> label == 0).count();

        if (positives == 0 || negatives == 0) {
            throw new IllegalArgumentException("AUC-ROC requires both positive and negative samples");
        }

        List<Integer> sortedIndexes = IntStream.range(0, probabilities.size())
                .boxed()
                .sorted(Comparator.comparing(probabilities::get).reversed())
                .toList();

        double previousFpr = 0.0;
        double previousTpr = 0.0;
        double auc = 0.0;

        int tp = 0;
        int fp = 0;

        for (Integer index : sortedIndexes) {
            if (actual.get(index) == 1) {
                tp++;
            } else if (actual.get(index) == 0) {
                fp++;
            } else {
                throw new IllegalArgumentException("Only binary labels 0/1 are supported");
            }

            double tpr = tp / (double) positives;
            double fpr = fp / (double) negatives;

            auc += (fpr - previousFpr) * (tpr + previousTpr) / 2.0;
            previousFpr = fpr;
            previousTpr = tpr;
        }

        return auc;
    }

    private static void validateSameSize(List<?> left, List<?> right) {
        if (left == null || right == null || left.size() != right.size() || left.isEmpty()) {
            throw new IllegalArgumentException("Lists must be non-null, non-empty and have the same size");
        }
    }

    private static double safeDivide(double numerator, double denominator) {
        return denominator == 0.0 ? 0.0 : numerator / denominator;
    }
}
