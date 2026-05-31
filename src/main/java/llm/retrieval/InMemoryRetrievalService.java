package llm.retrieval;

import java.util.Comparator;
import java.util.List;

public class InMemoryRetrievalService {

    private final List<DocumentChunk> corpus;
    private final EmbeddingService embeddingService;

    public InMemoryRetrievalService(List<DocumentChunk> corpus, EmbeddingService embeddingService) {
        this.corpus = corpus;
        this.embeddingService = embeddingService;
    }

    public List<SearchResult> search(String query, int topK) {
        double[] queryVector = embeddingService.embed(query);

        return corpus.stream()
                .map(document -> new SearchResult(
                        document,
                        cosineSimilarity(queryVector, embeddingService.embed(document.title() + " " + document.text()))
                ))
                .sorted(Comparator.comparing(SearchResult::score).reversed())
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
