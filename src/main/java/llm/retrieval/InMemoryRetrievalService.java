package llm.retrieval;

import java.util.Comparator;
import java.util.List;

public class InMemoryRetrievalService {

    private final java.util.List<DocumentChunk> corpus;
    private final llm.retrieval.EmbeddingService embeddingService;

    public InMemoryRetrievalService(java.util.List<DocumentChunk> corpus, llm.retrieval.EmbeddingService embeddingService) {
        this.corpus = corpus;
        this.embeddingService = embeddingService;
    }

    public java.util.List<SearchResult> search(String query, int topK) {
        double[] queryVector = embeddingService.embed(query);

        return corpus.stream()
                .map(document -> new llm.retrieval.SearchResult(
                        document,
                        cosineSimilarity(queryVector, embeddingService.embed(document.title() + " " + document.text()))
                ))
                .sorted(java.util.Comparator.comparing(llm.retrieval.SearchResult::score).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(double[] left, double[] right) {
        double dot = 0.0;
        double leftNorm = 0.0;
        double rightNorm = 0.0;

        for (int i = 0; i < left.length; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }

        if (leftNorm == 0.0 || rightNorm == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }
}
