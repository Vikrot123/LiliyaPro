package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextConsistencyContractTest {

    @Test
    fun cognitive_context_should_remain_consistent_with_runtime_state() {
        val composition = DefaultRuntimeComposition()

        val service = TestRuntimeService()
        composition.registerRuntimeService(service)

        composition.setModuleStates(
            mapOf(
                "CORE" to ModuleState.RUNNING
            )
        )

        composition.start()

        val running = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            running["runtimeState"]
        )

        assertEquals(
            listOf("consistency-service"),
            running["activeServices"]
        )

        assertEquals(
            mapOf("CORE" to ModuleState.RUNNING),
            running["moduleStates"]
        )

        assertEquals(
            null,
            running["failureReason"]
        )

        composition.markRuntimeFailed("consistency-failure")

        val failed = snapshot(composition)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed["runtimeState"]
        )

        assertEquals(
            "consistency-failure",
            failed["failureReason"]
        )

        assertTrue(
            failed["activeServices"] is List<*>
        )

        assertEquals(
            mapOf("CORE" to ModuleState.RUNNING),
            failed["moduleStates"]
        )

        composition.setFailureReason(null)
        composition.setRuntimeState(CoreRuntimeState.RUNNING)

        val recovered = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered["runtimeState"]
        )

        assertEquals(
            null,
            recovered["failureReason"]
        )
    }

    private fun snapshot(
        composition: DefaultRuntimeComposition
    ): Map<String, Any?> {
        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.WORKING)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot
                ?: error("Runtime cognitive context snapshot not found")

        return snapshot.values
    }

    private class TestRuntimeService : RuntimeService {

        override val name = "consistency-service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
