package pro.liliya.core.runtime.intelligence.knowledge.retrieval

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeRetriever {

    fun retrieve(
        query: RuntimeKnowledgeQuery,
        knowledge: List<RuntimeKnowledge>
    ): List<RuntimeKnowledgeRetrievalResult>
}
