package llm.retrieval;

import io.restassured.http.ContentType;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OllamaEmbeddingService implements EmbeddingService {

    private final String baseUri;
    private final String model;

    public OllamaEmbeddingService(String baseUri, String model) {
        this.baseUri = baseUri;
        this.model = model;
    }

    @Override
    public double[] embed(String text) {
        List<Float> embedding = given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(Map.of("model", model, "prompt", text))
                .post("/api/embeddings")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("embedding", Float.class);

        double[] result = new double[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            result[i] = embedding.get(i);
        }
        return result;
    }
}
