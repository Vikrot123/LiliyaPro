package pro.liliya.core.runtime.intelligence.knowledge.selection

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeSelectionResult(
    val knowledge: RuntimeKnowledge?,
    val relevantPoolUsed: Boolean,
    val reason: String,
    val selectionReason: RuntimeKnowledgeSelectionReason =
        RuntimeKnowledgeSelectionReason.FALLBACK_POOL,
    val relevanceScore: Double = 0.0
)
