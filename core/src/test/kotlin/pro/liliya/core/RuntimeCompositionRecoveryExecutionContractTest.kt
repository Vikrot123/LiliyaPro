package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryExecutionContractTest {

    @Test
    fun composition_recovers_failed_service_through_pipeline() {

        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        val registry = composition.runtimeServiceRegistry()

        val service = object : RuntimeService {

            override val name = "composition-recovery-service"

            override var state =
                RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        registry.register(service)

        composition.installRuntimeRecoveryEventBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "composition-recovery-service",
                reason = "composition-test",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            composition
                .runtimeSupervisor()
                .getRestartCount(
                    "composition-recovery-service"
                )
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        composition.uninstallRuntimeRecoveryEventBridge()

        RuntimeEventBus.clear()
    }
}
