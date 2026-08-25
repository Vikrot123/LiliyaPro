package pro.liliya.core.runtime.intelligence.context.cognitive

data class CognitiveContextSnapshot(
    val type: CognitiveContextType,
    val values: Map<String, Any?> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
