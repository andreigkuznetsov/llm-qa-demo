package llm;

import org.junit.jupiter.api.Tag;
import llm.util.MetricsUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class RetrievalMetricsTest {

    @Test
    void shouldCalculateRankingMetrics() {
        List<String> ranked = List.of("doc3", "doc1", "doc7", "doc2");
        Set<String> relevant = Set.of("doc1", "doc2");

        double pAt3 = MetricsUtils.precisionAtK(ranked, relevant, 3);
        double rAt3 = MetricsUtils.recallAtK(ranked, relevant, 3);
        double rr = MetricsUtils.reciprocalRank(ranked, relevant);

        assertThat(pAt3).isEqualTo(1.0 / 3.0);
        assertThat(rAt3).isEqualTo(0.5);
        assertThat(rr).isEqualTo(0.5);
    }
}
