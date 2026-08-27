package pro.liliya.core.runtime.intelligence.knowledge.quality

import pro.liliya.core.runtime.intelligence.knowledge.governance.RuntimeKnowledgeValidation
import pro.liliya.core.runtime.intelligence.knowledge.priority.RuntimeKnowledgePriority

data class RuntimeKnowledgeQualityAssessment(
    val validation: RuntimeKnowledgeValidation,
    val priority: RuntimeKnowledgePriority
)
