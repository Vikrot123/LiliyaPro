package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.service.DefaultCognitiveContextService
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.DefaultCognitiveContextSourceRegistry

class CognitiveContextServiceContractTest {

    private fun createContext(): CognitiveContext {
        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("source" to "test")
                )
            }
        }

        return object : CognitiveContext {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return source.snapshot(type)
            }

            override fun sources(): List<CognitiveContextSource> {
                return listOf(source)
            }
        }
    }

    @Test
    fun service_should_expose_context() {
        val context = createContext()
        val service = DefaultCognitiveContextService(
            cognitiveContext = context,
            cognitivePipeline = DefaultCognitiveContextPipeline(
                builder = DefaultCognitiveContextBuilder(),
                selector = DefaultCognitiveContextSelector()
            ),
            cognitiveSourceRegistry = DefaultCognitiveContextSourceRegistry()
        )

        assertNotNull(service.context())
        assertSame(context, service.context())
    }

    @Test
    fun service_should_delegate_snapshot() {
        val service = DefaultCognitiveContextService(
            cognitiveContext = createContext(),
            cognitivePipeline = DefaultCognitiveContextPipeline(
                builder = DefaultCognitiveContextBuilder(),
                selector = DefaultCognitiveContextSelector()
            ),
            cognitiveSourceRegistry = DefaultCognitiveContextSourceRegistry()
        )

        val snapshot = service.snapshot(
            CognitiveContextType.TASK
        )

        assertEquals(
            CognitiveContextType.TASK,
            snapshot.type
        )

        assertEquals(
            "test",
            snapshot.values["source"]
        )
    }

    @Test
    fun service_should_keep_same_context_instance() {
        val context = createContext()
        val service = DefaultCognitiveContextService(
            cognitiveContext = context,
            cognitivePipeline = DefaultCognitiveContextPipeline(
                builder = DefaultCognitiveContextBuilder(),
                selector = DefaultCognitiveContextSelector()
            ),
            cognitiveSourceRegistry = DefaultCognitiveContextSourceRegistry()
        )

        assertSame(
            context,
            service.context()
        )
    }

    @Test
    fun service_should_not_create_independent_context_state() {
        val context = createContext()
        val service = DefaultCognitiveContextService(
            cognitiveContext = context,
            cognitivePipeline = DefaultCognitiveContextPipeline(
                builder = DefaultCognitiveContextBuilder(),
                selector = DefaultCognitiveContextSelector()
            ),
            cognitiveSourceRegistry = DefaultCognitiveContextSourceRegistry()
        )

        assertEquals(
            context.snapshot(CognitiveContextType.WORKING),
            service.snapshot(CognitiveContextType.WORKING)
        )
    }
}
