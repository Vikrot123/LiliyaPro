package pro.liliya.core.runtime.intelligence.knowledge.revision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeRevisionManager {

    fun revise(
        previous: RuntimeKnowledge,
        updated: RuntimeKnowledge,
        reason: RuntimeKnowledgeRevisionReason
    ): RuntimeKnowledgeRevision
}
