package pro.liliya.core.runtime.intelligence.knowledge.retrieval

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.relevance.DefaultRuntimeKnowledgeRelevanceEvaluator
import pro.liliya.core.runtime.intelligence.knowledge.relevance.RuntimeKnowledgeRelevanceEvaluator

class DefaultRuntimeKnowledgeRetriever(
    private val relevanceEvaluator:
        RuntimeKnowledgeRelevanceEvaluator =
        DefaultRuntimeKnowledgeRelevanceEvaluator()
) : RuntimeKnowledgeRetriever {

    override fun retrieve(
        query: RuntimeKnowledgeQuery,
        knowledge: List<RuntimeKnowledge>
    ): List<RuntimeKnowledgeRetrievalResult> {

        return knowledge
            .mapNotNull { item ->
                val relevance =
                    relevanceEvaluator.evaluate(
                        statement = item.statement,
                        interpretation = query.text
                    )

                if (!relevance.relevant) {
                    null
                } else {
                    RuntimeKnowledgeRetrievalResult(
                        knowledge = item,
                        relevance = relevance.score,
                        reason =
                            if (relevance.score == 1.0) {
                                "Knowledge statement matches query"
                            } else {
                                "Knowledge statement is relevant to query"
                            }
                    )
                }
            }
            .sortedWith(
                compareByDescending<RuntimeKnowledgeRetrievalResult> {
                    it.relevance
                }.thenByDescending {
                    it.knowledge.confidence
                }.thenByDescending {
                    it.knowledge.createdAt
                }
            )
    }
}
