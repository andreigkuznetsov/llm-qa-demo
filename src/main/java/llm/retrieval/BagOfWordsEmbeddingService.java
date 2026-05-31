package llm.retrieval;

import llm.util.TextSimilarityUtils;

import java.util.List;
import java.util.Set;

public class BagOfWordsEmbeddingService implements llm.retrieval.EmbeddingService {

    private final java.util.List<String> vocabulary;

    public BagOfWordsEmbeddingService(java.util.List<DocumentChunk> corpus) {
        this.vocabulary = corpus.stream()
                .flatMap(doc -> llm.util.TextSimilarityUtils.tokenize(doc.title() + " " + doc.text()).stream())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public double[] embed(String text) {
        java.util.Set<String> tokens = llm.util.TextSimilarityUtils.tokenize(text);
        double[] vector = new double[vocabulary.size()];

        for (int i = 0; i < vocabulary.size(); i++) {
            vector[i] = tokens.contains(vocabulary.get(i)) ? 1.0 : 0.0;
        }

        return vector;
    }
}
