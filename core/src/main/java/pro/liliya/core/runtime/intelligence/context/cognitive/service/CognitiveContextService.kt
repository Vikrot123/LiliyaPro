package pro.liliya.core.runtime.intelligence.context.cognitive.service

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.CognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.CognitiveContextSourceRegistry
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria

interface CognitiveContextService {

    fun context(): CognitiveContext

    fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot

    fun pipeline(): CognitiveContextPipeline

    fun sourceRegistry(): CognitiveContextSourceRegistry

    fun registerSource(
        source: CognitiveContextSource
    ): Boolean

    fun unregisterSource(
        source: CognitiveContextSource
    ): Boolean

    fun process(
        type: CognitiveContextType,
        criteria: CognitiveContextSelectionCriteria =
            CognitiveContextSelectionCriteria()
    ): CognitiveContextSelection
}
