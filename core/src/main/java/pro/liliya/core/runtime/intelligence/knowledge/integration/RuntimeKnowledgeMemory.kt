package pro.liliya.core.runtime.intelligence.knowledge.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneAction
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneResult
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.RuntimeKnowledgeRetrievalResult
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

interface RuntimeKnowledgeMemory {

    fun remember(
        knowledge: RuntimeKnowledge
    )

    fun rememberWithHygiene(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeHygieneResult {
        remember(knowledge)

        return RuntimeKnowledgeHygieneResult(
            action = RuntimeKnowledgeHygieneAction.ADDED,
            requestedKnowledge = knowledge,
            retainedKnowledge = knowledge
        )
    }

    fun forget(
        knowledge: RuntimeKnowledge
    )

    fun associate(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    )

    fun query(
        text: String
    ): List<RuntimeKnowledgeGraphRankingResult>

    fun availableKnowledge(): List<RuntimeKnowledge> {
        return emptyList()
    }

    fun retrieveRelevant(
        text: String
    ): List<RuntimeKnowledgeRetrievalResult> {
        return emptyList()
    }

    fun getLifecycleState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState?
}
