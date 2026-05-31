package llm;

import org.junit.jupiter.api.Tag;
import llm.util.MetricsUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.jupiter.api.Tag("full")
public class RetrievalMetricsTest {

    @org.junit.jupiter.api.Test
    void shouldCalculateRankingMetrics() {
        java.util.List<String> ranked = java.util.List.of("doc3", "doc1", "doc7", "doc2");
        java.util.Set<String> relevant = java.util.Set.of("doc1", "doc2");

        double pAt3 = llm.util.MetricsUtils.precisionAtK(ranked, relevant, 3);
        double rAt3 = llm.util.MetricsUtils.recallAtK(ranked, relevant, 3);
        double rr = llm.util.MetricsUtils.reciprocalRank(ranked, relevant);

        assertThat(pAt3).isEqualTo(1.0 / 3.0);
        assertThat(rAt3).isEqualTo(0.5);
        assertThat(rr).isEqualTo(0.5);
    }
}
