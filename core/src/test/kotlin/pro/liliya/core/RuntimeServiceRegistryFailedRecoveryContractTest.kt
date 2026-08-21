package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState

class RuntimeServiceRegistryFailedRecoveryContractTest {

    @Test
    fun failed_restart_publishes_failure_and_not_recovered() {

        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        val registry = RuntimeServiceRegistry()

        val service = FailingRestartService()

        registry.register(service)

        registry.startAll()

        try {
            registry.restart("service")
        } catch (_: Exception) {
        }

        val failed =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        val recovered =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceRecovered>()

        assertEquals(
            1,
            failed.size
        )

        assertEquals(
            "service",
            failed.first().serviceName
        )

        assertFalse(
            recovered.any {
                it.serviceName == "service"
            }
        )
    }


    @AfterTest
    fun cleanup() {
        RuntimeEventBus.clear()
    }


    private class FailingRestartService : RuntimeService {

        override val name = "service"

        override var state =
            RuntimeServiceState.CREATED


        private var starts = 0


        override fun start() {
            starts++

            if (starts > 1) {
                throw IllegalStateException("restart failed")
            }

            state = RuntimeServiceState.RUNNING
        }


        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
