package pro.liliya.core.runtime.intelligence.context.cognitive.registry

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource

interface CognitiveContextSourceRegistry {

    fun register(source: CognitiveContextSource): Boolean

    fun unregister(source: CognitiveContextSource): Boolean

    fun sources(): List<CognitiveContextSource>

    fun clear()

    fun size(): Int
}
