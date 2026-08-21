package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryPipelineStopBarrierContractTest {

    @Test
    fun stopped_runtime_composition_does_not_start_recovery_pipeline() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()

        val service = TestService()

        composition.runtimeServiceRegistry()
            .register(service)

        composition.startRuntime()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        composition.stopRuntime()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "failure after stop",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )

        composition.stopRuntime()

        RuntimeEventBus.clear()
    }

    private class TestService : RuntimeService {

        override val name = "service"

        override var state =
            RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
