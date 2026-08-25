package pro.liliya.core.runtime.intelligence.context.cognitive.pipeline

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector

class DefaultCognitiveContextPipeline(
    private val builder: CognitiveContextBuilder,
    private val selector: CognitiveContextSelector
) : CognitiveContextPipeline {

    override fun process(
        type: CognitiveContextType,
        sources: List<CognitiveContextSource>,
        criteria: CognitiveContextSelectionCriteria
    ): CognitiveContextSelection {

        val snapshot = builder.build(
            type = type,
            sources = sources
        )

        return selector.select(
            snapshot = snapshot,
            criteria = criteria
        )
    }
}
