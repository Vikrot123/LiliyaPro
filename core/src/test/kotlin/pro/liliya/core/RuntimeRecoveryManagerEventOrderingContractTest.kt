package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerEventOrderingContractTest {

    @Test
    fun successful_recovery_emits_started_then_completed() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "ordering-service"

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

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        val events = mutableListOf<String>()

        manager.install()

        val recoveryBus =
            pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus()

        val managerWithBus = RuntimeRecoveryManager(
            supervisor,
            registry,
            recoveryBus
        )

        recoveryBus.subscribe { event ->
            events += event::class.simpleName ?: "unknown"
        }

        managerWithBus.recover("ordering-service")

        assertEquals(
            listOf(
                "Started",
                "Completed"
            ),
            events
        )

        RuntimeEventBus.clear()
    }
}
