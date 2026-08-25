package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextServiceLifecycleContractTest {

    @Test
    fun registered_runtime_service_should_flow_into_cognitive_context_through_lifecycle() {
        val composition = DefaultRuntimeComposition()

        val service = TestRuntimeService()

        composition.registerRuntimeService(service)

        assertEquals(
            emptyList(),
            activeServices(composition)
        )

        composition.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        assertEquals(
            listOf("lifecycle-service"),
            activeServices(composition)
        )

        composition.stop()

        assertEquals(
            emptyList(),
            activeServices(composition)
        )
    }

    private fun activeServices(
        composition: DefaultRuntimeComposition
    ): List<String> {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot = result.values["source_0"] as? CognitiveContextSnapshot
            ?: error("Runtime cognitive context snapshot not found")

        @Suppress("UNCHECKED_CAST")
        return snapshot.values["activeServices"] as List<String>
    }

    private class TestRuntimeService : RuntimeService {

        override val name = "lifecycle-service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
