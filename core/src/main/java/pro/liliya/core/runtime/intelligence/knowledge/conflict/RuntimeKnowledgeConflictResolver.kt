package pro.liliya.core.runtime.intelligence.knowledge.conflict

interface RuntimeKnowledgeConflictResolver {

    fun resolve(
        conflict: RuntimeKnowledgeConflict
    ): RuntimeKnowledgeConflictResolution
}
