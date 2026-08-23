package pro.liliya.core.runtime.intelligence.knowledge.graph

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType

interface RuntimeKnowledgeGraphBuilder {

    fun connect(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ): RuntimeKnowledgeGraphEdge
}
