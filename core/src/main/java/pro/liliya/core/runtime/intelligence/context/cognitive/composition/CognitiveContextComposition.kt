package pro.liliya.core.runtime.intelligence.context.cognitive.composition

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle.CognitiveContextLifecycle
import pro.liliya.core.runtime.intelligence.context.cognitive.service.CognitiveContextService
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.CognitiveContextSourceRegistry

interface CognitiveContextComposition {

    fun context(): CognitiveContext

    fun lifecycle(): CognitiveContextLifecycle

    fun service(): CognitiveContextService

    fun sourceRegistry(): CognitiveContextSourceRegistry
}
