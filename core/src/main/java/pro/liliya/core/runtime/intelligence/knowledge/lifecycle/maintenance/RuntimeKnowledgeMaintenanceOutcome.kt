package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.decision.RuntimeKnowledgeLifecycleDecision

data class RuntimeKnowledgeMaintenanceOutcome(
    val knowledge: RuntimeKnowledge,
    val decision: RuntimeKnowledgeLifecycleDecision,
    val executed: Boolean
)
