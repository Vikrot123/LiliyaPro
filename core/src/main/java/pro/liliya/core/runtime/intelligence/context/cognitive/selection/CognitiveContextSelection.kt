package pro.liliya.core.runtime.intelligence.context.cognitive.selection

data class CognitiveContextSelection(
    val values: Map<String, Any?>,
    val selectedCount: Int,
    val rejectedCount: Int
)
