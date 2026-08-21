package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryObserverVisibilityContractTest {

    @Test
    fun recovered_runtime_state_is_visible_to_observers_once() {
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
                reason = "observer recovery failure",
                sourceRegistry = composition.runtimeServiceRegistry()
            )
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
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
