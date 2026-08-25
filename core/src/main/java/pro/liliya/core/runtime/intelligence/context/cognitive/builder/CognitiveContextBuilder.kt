package pro.liliya.core.runtime.intelligence.context.cognitive.builder

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

interface CognitiveContextBuilder {

    fun build(
        type: CognitiveContextType,
        sources: List<CognitiveContextSource>
    ): CognitiveContextSnapshot
}
