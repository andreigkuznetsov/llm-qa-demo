package llm.model;

public record JudgeScores(
        Integer relevance,
        Integer completeness,
        Integer safety,
        Integer accuracy,
        Integer faithfulness,
        Integer clarity,
        String comment
) {
}
