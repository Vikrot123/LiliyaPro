package pro.liliya.core.runtime.intelligence.context.cognitive.service

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.CognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.CognitiveContextSourceRegistry
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria

class DefaultCognitiveContextService(
    private val cognitiveContext: CognitiveContext,
    private val cognitivePipeline: CognitiveContextPipeline,
    private val cognitiveSourceRegistry: CognitiveContextSourceRegistry
) : CognitiveContextService {

    override fun context(): CognitiveContext {
        return cognitiveContext
    }

    override fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot {
        return cognitiveContext.snapshot(type)
    }

    override fun pipeline(): CognitiveContextPipeline {
        return cognitivePipeline
    }

    override fun sourceRegistry(): CognitiveContextSourceRegistry {
        return cognitiveSourceRegistry
    }

    override fun registerSource(
        source: CognitiveContextSource
    ): Boolean {
        return cognitiveSourceRegistry.register(source)
    }

    override fun unregisterSource(
        source: CognitiveContextSource
    ): Boolean {
        return cognitiveSourceRegistry.unregister(source)
    }

    override fun process(
        type: CognitiveContextType,
        criteria: CognitiveContextSelectionCriteria
    ): CognitiveContextSelection {
        return cognitivePipeline.process(
            type = type,
            sources = cognitiveSourceRegistry.sources(),
            criteria = criteria
        )
    }
}
