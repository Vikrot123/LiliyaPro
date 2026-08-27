package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

data class RuntimeDecision(
    val command: RuntimeCommand?,
    val reason: String,
    val confidence: Double,
    val knowledgeSelection: RuntimeKnowledgeSelectionResult? = null
)
