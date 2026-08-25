package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextRecoveryConsistencyContractTest {

    @Test
    fun cognitive_context_should_reflect_successful_runtime_recovery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()
        val service = RecoverableService()

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()
        composition.runtimeServiceRegistry().register(service)

        composition.startRuntime()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        composition.markRuntimeFailed("recovery-failure")

        val failed = snapshot(composition)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            failed["runtimeState"]
        )

        assertEquals(
            "recovery-failure",
            failed["failureReason"]
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "recovery-failure",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            1,
            composition.runtimeSupervisor()
                .getRestartCount(service.name)
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        composition.setFailureReason(null)
        composition.setRuntimeState(CoreRuntimeState.RUNNING)

        val recovered = snapshot(composition)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            recovered["runtimeState"]
        )

        assertNull(
            recovered["failureReason"]
        )

        assertTrue(
            recovered["activeServices"] is List<*>
        )

        assertEquals(
            listOf(service.name),
            recovered["activeServices"]
        )

        RuntimeEventBus.clear()
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

    private class RecoverableService : RuntimeService {

        override val name = "cognitive-recovery-service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
