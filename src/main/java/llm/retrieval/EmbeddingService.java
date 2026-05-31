package llm.retrieval;

public interface EmbeddingService {
    double[] embed(String text);
}
