package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeFailureRestartMultiCompositionIsolationContractTest {

    @Test
    fun recovery_failure_restart_multi_composition_isolation_is_preserved() {
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeRecoveryEventBridge()
        second.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val beforeRestart =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        second.uninstallRuntimeRecoveryEventBridge()
        second.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service-after-restart"
            )
        )

        val afterRestart =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            4,
            afterRestart.size
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
