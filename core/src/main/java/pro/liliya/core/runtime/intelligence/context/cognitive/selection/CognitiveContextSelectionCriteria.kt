package pro.liliya.core.runtime.intelligence.context.cognitive.selection

data class CognitiveContextSelectionCriteria(
    val minimumRelevance: Double = 0.0,
    val minimumImportance: Double = 0.0,
    val minimumConfidence: Double = 0.0,
    val maximumAgeMillis: Long? = null
)
