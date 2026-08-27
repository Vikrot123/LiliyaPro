package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision

data class RuntimeKnowledgeMaintenanceReport(
    val outcomes: List<RuntimeKnowledgeMaintenanceOutcome>
) {
    val processedCount: Int
        get() = outcomes.size

    val keptCount: Int
        get() =
            count(
                RuntimeKnowledgeLifecycleDecision.KEEP
            )

    val reviewedCount: Int
        get() =
            count(
                RuntimeKnowledgeLifecycleDecision.REVIEW
            )

    val archivedCount: Int
        get() =
            count(
                RuntimeKnowledgeLifecycleDecision.ARCHIVE
            )

    private fun count(
        decision: RuntimeKnowledgeLifecycleDecision
    ): Int {
        return outcomes.count {
            it.decision == decision
        }
    }
}
