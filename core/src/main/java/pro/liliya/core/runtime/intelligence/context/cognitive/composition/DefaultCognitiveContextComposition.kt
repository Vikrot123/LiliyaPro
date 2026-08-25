package pro.liliya.core.runtime.intelligence.context.cognitive.composition

import pro.liliya.core.CoreRuntimeStateHolder

import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.intelligence.context.DefaultRuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle.CognitiveContextLifecycle
import pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle.DefaultCognitiveContextLifecycle
import pro.liliya.core.runtime.intelligence.context.cognitive.service.CognitiveContextService
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.CognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.service.DefaultCognitiveContextService
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.CognitiveContextSourceRegistry
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.DefaultCognitiveContextSourceRegistry
import pro.liliya.core.runtime.intelligence.context.cognitive.source.DefaultRuntimeCognitiveContextSource

class DefaultCognitiveContextComposition(
    private val runtimeContextProvider: RuntimeContextProvider =
        DefaultRuntimeContextProvider(
            registry = RuntimeServiceRegistry(),
            runtimeStateHolder = CoreRuntimeStateHolder()
        )
) : CognitiveContextComposition {

    private val lifecycle: CognitiveContextLifecycle =
        DefaultCognitiveContextLifecycle()

    private val builder: CognitiveContextBuilder =
        DefaultCognitiveContextBuilder()

    private val selector: CognitiveContextSelector =
        DefaultCognitiveContextSelector()

    private val sourceRegistry: CognitiveContextSourceRegistry =
        DefaultCognitiveContextSourceRegistry()

    private val runtimeSource =
        DefaultRuntimeCognitiveContextSource(runtimeContextProvider)

    init {
        sourceRegistry.register(runtimeSource)
    }


    private val pipeline: CognitiveContextPipeline =
        DefaultCognitiveContextPipeline(
            builder = builder,
            selector = selector
        )

    private val context: CognitiveContext =
        object : CognitiveContext {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                val values = linkedMapOf<String, Any?>()

                sourceRegistry.sources().forEach { source ->
                    val snapshot = source.snapshot(type)

                    if (snapshot != null) {
                        values.putAll(snapshot.values)
                    }
                }

                return CognitiveContextSnapshot(
                    type = type,
                    values = values
                )
            }

            override fun sources(): List<CognitiveContextSource> {
                return sourceRegistry.sources()
            }
        }

    private val service: CognitiveContextService =
        DefaultCognitiveContextService(
            cognitiveContext = context,
            cognitivePipeline = pipeline,
            cognitiveSourceRegistry = sourceRegistry
        )

    override fun context(): CognitiveContext {
        return context
    }

    override fun lifecycle(): CognitiveContextLifecycle {
        return lifecycle
    }

    fun pipeline(): CognitiveContextPipeline {
        return pipeline
    }

    override fun service(): CognitiveContextService {
        return service
    }

    override fun sourceRegistry(): CognitiveContextSourceRegistry {
        return sourceRegistry
    }
}
