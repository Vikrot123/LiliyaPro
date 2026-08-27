package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot

class RuntimeCompositionCognitiveContextIntegrationContractTest {

    @Test
    fun runtime_composition_should_expose_cognitive_context_composition() {
        val composition = DefaultRuntimeComposition()

        assertSame(
            composition.cognitiveContextComposition(),
            composition.cognitiveContextComposition()
        )
    }

    @Test
    fun cognitive_context_should_be_composition_owned() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.cognitiveContextComposition(),
            second.cognitiveContextComposition()
        )
    }

    @Test
    fun cognitive_context_should_use_runtime_composition_registry() {
        val composition = DefaultRuntimeComposition()

        val runtimeRegistry = composition.runtimeServiceRegistry()
        val cognitiveRegistry =
            composition
                .cognitiveContextComposition()
                .sourceRegistry()

        assertEquals(2, cognitiveRegistry.size())

        runtimeRegistry.register(
            object : RuntimeService {
                override val name = "integration-service"
                override var state = RuntimeServiceState.RUNNING

                override fun start() {}
                override fun stop() {}
            }
        )

        val result =
            composition
                .cognitiveContextComposition()
                .service()
                .process(
                    pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType.WORKING
                )

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)

        val sourceSnapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        check(sourceSnapshot != null)

        assertEquals(
            listOf("integration-service"),
            sourceSnapshot.values["activeServices"]
        )
    }

    @Test
    fun separate_runtime_compositions_should_keep_runtime_context_isolated() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.runtimeServiceRegistry().register(
            object : RuntimeService {
                override val name = "first-only-service"
                override var state = RuntimeServiceState.RUNNING

                override fun start() {}
                override fun stop() {}
            }
        )

        assertEquals(
            1,
            first.runtimeServiceRegistry().getStates().size
        )

        assertEquals(
            0,
            second.runtimeServiceRegistry().getStates().size
        )

        assertNotSame(
            first.cognitiveContextComposition(),
            second.cognitiveContextComposition()
        )
    }
}
