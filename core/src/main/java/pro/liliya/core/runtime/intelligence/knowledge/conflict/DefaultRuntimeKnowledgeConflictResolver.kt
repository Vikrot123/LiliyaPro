package pro.liliya.core.runtime.intelligence.knowledge.conflict

class DefaultRuntimeKnowledgeConflictResolver :
    RuntimeKnowledgeConflictResolver {

    override fun resolve(
        conflict: RuntimeKnowledgeConflict
    ): RuntimeKnowledgeConflictResolution {

        return when {
            conflict.first.confidence > conflict.second.confidence ->
                RuntimeKnowledgeConflictResolution(
                    resolved = true,
                    selectedKnowledge = conflict.first,
                    reason = "First knowledge has higher confidence"
                )

            conflict.second.confidence > conflict.first.confidence ->
                RuntimeKnowledgeConflictResolution(
                    resolved = true,
                    selectedKnowledge = conflict.second,
                    reason = "Second knowledge has higher confidence"
                )

            else ->
                RuntimeKnowledgeConflictResolution(
                    resolved = false,
                    selectedKnowledge = null,
                    reason = "Knowledge confidence is equal"
                )
        }
    }
}
