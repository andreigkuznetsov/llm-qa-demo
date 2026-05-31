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

@org.junit.jupiter.api.Tag("full")
public class RetrievalCorpusTest extends llm.BaseLlmTest {

    @org.junit.jupiter.api.Test
    void shouldRetrieveRelevantDocumentsFromLocalCorpus() throws Exception {
        java.util.List<llm.retrieval.DocumentChunk> corpus = loadCorpus();
        llm.retrieval.InMemoryRetrievalService retrievalService = new llm.retrieval.InMemoryRetrievalService(
                corpus,
                new llm.retrieval.BagOfWordsEmbeddingService(corpus)
        );

        java.util.List<llm.retrieval.SearchResult> results = retrievalService.search("How should API reject file bigger than 10MB?", 3);
        java.util.List<String> rankedIds = results.stream()
                .map(result -> result.document().id())
                .toList();

        java.util.Set<String> relevantIds = java.util.Set.of("upload-file-size-doc");

        assertThat(rankedIds).contains("upload-file-size-doc");
        assertThat(llm.util.MetricsUtils.precisionAtK(rankedIds, relevantIds, 3)).isGreaterThanOrEqualTo(1.0 / 3.0);
        assertThat(llm.util.MetricsUtils.recallAtK(rankedIds, relevantIds, 3)).isEqualTo(1.0);
        assertThat(llm.util.MetricsUtils.reciprocalRank(rankedIds, relevantIds)).isEqualTo(1.0);
    }

    private java.util.List<llm.retrieval.DocumentChunk> loadCorpus() throws Exception {
        try (java.io.InputStream in = getClass().getClassLoader().getResourceAsStream("retrieval-corpus.json")) {
            return objectMapper.readValue(in, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
        }
    }
}
