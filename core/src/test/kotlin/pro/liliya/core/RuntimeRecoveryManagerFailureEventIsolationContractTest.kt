package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerFailureEventIsolationContractTest {

    @Test
    fun failed_recovery_emits_only_failed_event() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val recoveryBus = RuntimeRecoveryEventBus()

        recoveryBus.subscribe {
            events.add(it)
        }

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "failed-recovery-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                throw IllegalStateException("start failure")
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry,
            recoveryBus
        )

        val result = manager.recover(
            "failed-recovery-service"
        )

        assertFalse(result)

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("failed-recovery-service"),
                RuntimeRecoveryEvent.Failed("failed-recovery-service")
            ),
            events
        )

        RuntimeEventBus.clear()
    }
}
