package llm.retrieval;

import llm.util.TextSimilarityUtils;

import java.util.List;
import java.util.Set;

public class BagOfWordsEmbeddingService implements EmbeddingService {

    private final List<String> vocabulary;

    public BagOfWordsEmbeddingService(List<DocumentChunk> corpus) {
        this.vocabulary = corpus.stream()
                .flatMap(doc -> TextSimilarityUtils.tokenize(doc.title() + " " + doc.text()).stream())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public double[] embed(String text) {
        Set<String> tokens = TextSimilarityUtils.tokenize(text);
        double[] vector = new double[vocabulary.size()];

        for (int i = 0; i < vocabulary.size(); i++) {
            vector[i] = tokens.contains(vocabulary.get(i)) ? 1.0 : 0.0;
        }

        return vector;
    }
}
