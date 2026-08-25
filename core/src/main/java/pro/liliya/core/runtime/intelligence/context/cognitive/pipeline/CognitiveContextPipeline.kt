package pro.liliya.core.runtime.intelligence.context.cognitive.pipeline

import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria

interface CognitiveContextPipeline {

    fun process(
        type: CognitiveContextType,
        sources: List<CognitiveContextSource>,
        criteria: CognitiveContextSelectionCriteria =
            CognitiveContextSelectionCriteria()
    ): CognitiveContextSelection
}
