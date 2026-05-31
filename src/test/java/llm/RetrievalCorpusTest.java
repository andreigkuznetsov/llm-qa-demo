package llm;

import com.fasterxml.jackson.core.type.TypeReference;
import llm.retrieval.BagOfWordsEmbeddingService;
import llm.retrieval.DocumentChunk;
import llm.retrieval.InMemoryRetrievalService;
import llm.retrieval.SearchResult;
import llm.util.MetricsUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("full")
public class RetrievalCorpusTest extends BaseLlmTest {

    @Test
    void shouldRetrieveRelevantDocumentsFromLocalCorpus() throws Exception {
        List<DocumentChunk> corpus = loadCorpus();
        InMemoryRetrievalService retrievalService = new InMemoryRetrievalService(
                corpus,
                new BagOfWordsEmbeddingService(corpus)
        );

        List<SearchResult> results = retrievalService.search("How should API reject file bigger than 10MB?", 3);
        List<String> rankedIds = results.stream()
                .map(result -> result.document().id())
                .toList();

        Set<String> relevantIds = Set.of("upload-file-size-doc");

        assertThat(rankedIds).contains("upload-file-size-doc");
        assertThat(MetricsUtils.precisionAtK(rankedIds, relevantIds, 3)).isGreaterThanOrEqualTo(1.0 / 3.0);
        assertThat(MetricsUtils.recallAtK(rankedIds, relevantIds, 3)).isEqualTo(1.0);
        assertThat(MetricsUtils.reciprocalRank(rankedIds, relevantIds)).isEqualTo(1.0);
    }

    private List<DocumentChunk> loadCorpus() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("retrieval-corpus.json")) {
            return objectMapper.readValue(in, new TypeReference<>() {
            });
        }
    }
}
