package pro.liliya.core.runtime.intelligence.context.cognitive.builder

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class DefaultCognitiveContextBuilder :
    CognitiveContextBuilder {

    override fun build(
        type: CognitiveContextType,
        sources: List<CognitiveContextSource>
    ): CognitiveContextSnapshot {

        val values = linkedMapOf<String, Any?>()

        sources.forEachIndexed { index, source ->
            try {
                val snapshot = source.snapshot(type)

                if (snapshot != null) {
                    values["source_$index"] = snapshot
                }
            } catch (_: Exception) {
                // A failing source must not prevent later sources
                // from contributing to the cognitive context.
            }
        }

        return CognitiveContextSnapshot(
            type = type,
            values = values
        )
    }
}
