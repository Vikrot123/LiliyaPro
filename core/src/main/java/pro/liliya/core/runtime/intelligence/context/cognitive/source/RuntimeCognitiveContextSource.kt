package pro.liliya.core.runtime.intelligence.context.cognitive.source

import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

interface RuntimeCognitiveContextSource : CognitiveContextSource {
    override fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot?
}
