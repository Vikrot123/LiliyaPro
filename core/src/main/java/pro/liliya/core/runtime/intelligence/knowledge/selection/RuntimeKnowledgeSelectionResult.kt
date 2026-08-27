package pro.liliya.core.runtime.intelligence.knowledge.selection

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeSelectionResult(
    val knowledge: RuntimeKnowledge?,
    val relevantPoolUsed: Boolean,
    val reason: String
)
