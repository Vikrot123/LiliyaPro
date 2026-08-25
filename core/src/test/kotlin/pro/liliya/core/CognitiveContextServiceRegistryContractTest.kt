package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.registry.DefaultCognitiveContextSourceRegistry
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.service.DefaultCognitiveContextService

class CognitiveContextServiceRegistryContractTest {

    private fun service(): DefaultCognitiveContextService {
        return DefaultCognitiveContextService(
            cognitiveContext = object :
                pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext {

                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot {
                    return CognitiveContextSnapshot(type)
                }

                override fun sources(): List<CognitiveContextSource> {
                    return emptyList()
                }
            },
            cognitivePipeline = DefaultCognitiveContextPipeline(
                builder = DefaultCognitiveContextBuilder(),
                selector = DefaultCognitiveContextSelector()
            ),
            cognitiveSourceRegistry =
                DefaultCognitiveContextSourceRegistry()
        )
    }

    private fun source(
        value: String
    ): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to value)
                )
            }
        }
    }

    @Test
    fun service_should_expose_source_registry() {
        val service = service()

        assertSame(
            service.sourceRegistry(),
            service.sourceRegistry()
        )
    }

    @Test
    fun service_should_register_sources_through_registry() {
        val service = service()
        val source = source("runtime")

        assertTrue(service.registerSource(source))
        assertEquals(
            1,
            service.sourceRegistry().size()
        )
    }

    @Test
    fun service_should_reject_duplicate_source() {
        val service = service()
        val source = source("runtime")

        assertTrue(service.registerSource(source))
        assertFalse(service.registerSource(source))

        assertEquals(
            1,
            service.sourceRegistry().size()
        )
    }

    @Test
    fun service_should_unregister_source_through_registry() {
        val service = service()
        val source = source("runtime")

        service.registerSource(source)

        assertTrue(service.unregisterSource(source))
        assertEquals(
            0,
            service.sourceRegistry().size()
        )
    }

    @Test
    fun process_should_use_registered_sources_automatically() {
        val service = service()

        service.registerSource(source("runtime"))

        val result = service.process(
            type = CognitiveContextType.TASK
        )

        assertEquals(
            1,
            result.selectedCount
        )

        assertEquals(
            0,
            result.rejectedCount
        )

        assertTrue(
            result.values.containsKey("source_0")
        )
    }

    @Test
    fun process_should_use_current_registry_state() {
        val service = service()

        val first = source("first")
        val second = source("second")

        service.registerSource(first)

        val firstResult = service.process(
            CognitiveContextType.WORKING
        )

        service.registerSource(second)

        val secondResult = service.process(
            CognitiveContextType.WORKING
        )

        assertEquals(
            1,
            firstResult.selectedCount
        )

        assertEquals(
            2,
            secondResult.selectedCount
        )
    }
}
