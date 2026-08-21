package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryPipelineContractTest {

    @Test
    fun runtime_composition_recovery_pipeline_recovers_failed_service_once() {
        RuntimeEventBus.clear()

        val composition =
            DefaultRuntimeComposition()

        val recoveredEvents = mutableListOf<String>()

        RuntimeEventBus.subscribe { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recoveredEvents += event.serviceName
            }
        }

        composition.prepareRuntime()

        composition.installRuntimeRecoveryEventBridge()

        val service = TestService()

        composition.runtimeServiceRegistry()
            .register(service)

        composition.startRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "pipeline failure"
            )
        )

        assertEquals(
            listOf("service"),
            recoveredEvents
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "second failure"
            )
        )

        assertEquals(
            listOf(
                "service",
                "service"
            ),
            recoveredEvents
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
