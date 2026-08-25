package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class RuntimeCompositionCognitiveContextServiceRecoveryIsolationContractTest {

    @Test
    fun service_recovery_should_not_falsify_core_runtime_state_in_cognitive_context() {
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

        composition.markRuntimeFailed("service-failure")

        assertEquals(
            CoreRuntimeState.FAILED,
            composition.runtimeState()
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "service-failure",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        val snapshot = snapshot(composition)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            snapshot["runtimeState"]
        )

        assertEquals(
            service.name,
            (snapshot["activeServices"] as List<*>).single()
        )

        assertEquals(
            "service-failure",
            snapshot["failureReason"]
        )

        composition.stopRuntime()
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
        override val name = "isolated-recovery-service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
