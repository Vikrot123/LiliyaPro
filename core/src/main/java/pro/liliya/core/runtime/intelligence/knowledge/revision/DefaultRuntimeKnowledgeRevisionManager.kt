package pro.liliya.core.runtime.intelligence.knowledge.revision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeRevisionManager :
    RuntimeKnowledgeRevisionManager {

    override fun revise(
        previous: RuntimeKnowledge,
        updated: RuntimeKnowledge,
        reason: RuntimeKnowledgeRevisionReason
    ): RuntimeKnowledgeRevision {

        return RuntimeKnowledgeRevision(
            previous = previous,
            updated = updated,
            reason = reason,
            createdAt = System.currentTimeMillis()
        )
    }
}
