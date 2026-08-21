package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryPipelineRestartCycleContractTest {

    @Test
    fun restart_cycle_does_not_leak_previous_recovery_pipeline_state() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()
        val recovered = mutableListOf<String>()

        RuntimeEventBus.subscribe { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recovered += event.serviceName
            }
        }

        val service = TestService()

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()

        composition.runtimeServiceRegistry()
            .register(service)

        composition.startRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "first cycle failure",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            listOf("service"),
            recovered
        )

        composition.stopRuntime()

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()

        composition.startRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "second cycle failure",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            listOf("service"),
            recovered
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
