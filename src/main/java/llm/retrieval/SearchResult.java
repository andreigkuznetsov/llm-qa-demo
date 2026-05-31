package llm.retrieval;

public record SearchResult(
        DocumentChunk document,
        double score
) {
}
