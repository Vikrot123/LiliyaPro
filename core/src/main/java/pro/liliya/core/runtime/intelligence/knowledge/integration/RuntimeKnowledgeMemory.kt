package pro.liliya.core.runtime.intelligence.knowledge.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult

interface RuntimeKnowledgeMemory {

    fun remember(
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
}
