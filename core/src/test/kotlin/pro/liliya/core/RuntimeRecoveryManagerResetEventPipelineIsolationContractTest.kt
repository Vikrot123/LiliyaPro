package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerResetEventPipelineIsolationContractTest {

    @Test
    fun reset_clears_recovery_state_and_event_pipeline() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "reset-pipeline-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val recoveryBus = RuntimeRecoveryEventBus()

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry,
            recoveryBus
        )

        manager.recover("reset-pipeline-service")

        manager.reset()

        val snapshot = manager.snapshot()

        assertNull(snapshot.lastRecoveredService)
        assertNull(snapshot.lastRecoverySuccessful)

        val events = mutableListOf<RuntimeRecoveryEvent>()

        recoveryBus.subscribe {
            events.add(it)
        }

        assertEquals(
            emptyList(),
            events
        )

        RuntimeEventBus.clear()
    }
}
