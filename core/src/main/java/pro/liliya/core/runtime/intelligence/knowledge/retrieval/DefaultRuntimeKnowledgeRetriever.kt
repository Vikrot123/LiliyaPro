package pro.liliya.core.runtime.intelligence.knowledge.retrieval

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeRetriever :
    RuntimeKnowledgeRetriever {

    override fun retrieve(
        query: RuntimeKnowledgeQuery,
        knowledge: List<RuntimeKnowledge>
    ): List<RuntimeKnowledgeRetrievalResult> {

        return knowledge
            .filter {
                it.statement.contains(
                    query.text,
                    ignoreCase = true
                )
            }
            .map {
                RuntimeKnowledgeRetrievalResult(
                    knowledge = it,
                    relevance = 1.0,
                    reason = "Knowledge statement matches query"
                )
            }
    }
}
