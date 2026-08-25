package pro.liliya.core

import pro.liliya.core.CoreRuntimeStateHolder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.intelligence.context.DefaultRuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContext
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionRuntimeSourceContractTest {

    private fun provider(): RuntimeContextProvider {
        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {
                override val name = "runtime-service"
                override var state = RuntimeServiceState.RUNNING

                override fun start() {}
                override fun stop() {}
            }
        )

        return DefaultRuntimeContextProvider(
            registry = registry,
            runtimeStateHolder = CoreRuntimeStateHolder()
        )
    }

    @Test
    fun composition_should_register_runtime_source_automatically() {
        val composition = DefaultCognitiveContextComposition(provider())

        assertEquals(1, composition.sourceRegistry().size())
    }

    @Test
    fun runtime_source_should_be_part_of_composition_registry() {
        val composition = DefaultCognitiveContextComposition(provider())

        assertEquals(1, composition.sourceRegistry().sources().size)
        assertNotNull(composition.sourceRegistry().sources().first())
    }

    @Test
    fun service_should_process_runtime_context_without_manual_source_registration() {
        val composition = DefaultCognitiveContextComposition(provider())

        val result = composition.service().process(
            type = CognitiveContextType.TASK
        )

        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertNotNull(result.values["source_0"])
    }

    @Test
    fun runtime_context_should_contain_active_services() {
        val composition = DefaultCognitiveContextComposition(provider())

        val result = composition.service().process(
            type = CognitiveContextType.WORKING
        )

        val sourceSnapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(sourceSnapshot)

        assertEquals(
            CognitiveContextType.WORKING,
            sourceSnapshot.type
        )
    }

    @Test
    fun separate_compositions_should_have_independent_runtime_sources() {
        val first = DefaultCognitiveContextComposition(provider())
        val second = DefaultCognitiveContextComposition(provider())

        assertSame(
            first.sourceRegistry().sources().size,
            second.sourceRegistry().sources().size
        )

        assertEquals(1, first.sourceRegistry().size())
        assertEquals(1, second.sourceRegistry().size())

        assert(
            first.sourceRegistry().sources().first() !==
                second.sourceRegistry().sources().first()
        )
    }
}
