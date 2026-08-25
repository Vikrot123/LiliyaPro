package pro.liliya.core.runtime.intelligence.context.cognitive

import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata

data class CognitiveContextSnapshot(
    val type: CognitiveContextType,
    val values: Map<String, Any?> = emptyMap(),
    val metadata: Map<String, CognitiveContextValueMetadata> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
