package llm.retrieval;

public record SearchResult(
        llm.retrieval.DocumentChunk document,
        double score
) {
}
