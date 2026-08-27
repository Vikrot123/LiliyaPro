package pro.liliya.core.runtime.intelligence.meaning

import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

data class RuntimeMeaningResult(
    val interpretation: String,
    val confidence: Double,
    val significance: RuntimeMeaningSignificance,
    val generatedAt: Long,
    val knowledgeSelection: RuntimeKnowledgeSelectionResult? = null
)
