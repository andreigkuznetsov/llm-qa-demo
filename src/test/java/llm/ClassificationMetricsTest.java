package llm;

import llm.metrics.ClassificationMetrics;
import llm.metrics.ClassificationMetricsUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Tag("full")
public class ClassificationMetricsTest {

    @Test
    void shouldCalculateConfusionMatrixAndCoreMetrics() {
        List<Integer> actual = List.of(1, 1, 0, 0, 1);
        List<Integer> predicted = List.of(1, 0, 0, 0, 1);

        ClassificationMetrics metrics = ClassificationMetricsUtils.calculate(actual, predicted);

        assertThat(metrics.truePositive()).isEqualTo(2);
        assertThat(metrics.falsePositive()).isZero();
        assertThat(metrics.trueNegative()).isEqualTo(2);
        assertThat(metrics.falseNegative()).isEqualTo(1);
        assertThat(metrics.accuracy()).isCloseTo(0.8, within(0.001));
        assertThat(metrics.precision()).isCloseTo(1.0, within(0.001));
        assertThat(metrics.recall()).isCloseTo(0.666, within(0.001));
        assertThat(metrics.f1()).isCloseTo(0.8, within(0.001));
    }

    @Test
    void shouldCalculateAucRoc() {
        List<Integer> actual = List.of(1, 1, 0, 0);
        List<Double> probabilities = List.of(0.95, 0.80, 0.40, 0.10);

        double auc = ClassificationMetricsUtils.aucRoc(actual, probabilities);

        assertThat(auc).isCloseTo(1.0, within(0.001));
    }
}
