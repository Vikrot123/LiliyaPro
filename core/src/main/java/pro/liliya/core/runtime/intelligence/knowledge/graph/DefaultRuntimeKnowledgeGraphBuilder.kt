package pro.liliya.core.runtime.intelligence.knowledge.graph

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType

class DefaultRuntimeKnowledgeGraphBuilder :
    RuntimeKnowledgeGraphBuilder {

    override fun connect(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ): RuntimeKnowledgeGraphEdge {

        return RuntimeKnowledgeGraphEdge(
            source = RuntimeKnowledgeGraphNode(
                knowledge = source,
                createdAt = System.currentTimeMillis()
            ),
            target = RuntimeKnowledgeGraphNode(
                knowledge = target,
                createdAt = System.currentTimeMillis()
            ),
            type = type,
            createdAt = System.currentTimeMillis()
        )
    }
}
