package pro.liliya.core.runtime.intelligence.knowledge.relevance

interface RuntimeKnowledgeRelevanceEvaluator {

    fun evaluate(
        statement: String,
        interpretation: String
    ): RuntimeKnowledgeRelevance
}
