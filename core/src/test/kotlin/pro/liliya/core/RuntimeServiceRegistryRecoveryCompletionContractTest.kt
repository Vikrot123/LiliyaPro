package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState

class RuntimeServiceRegistryRecoveryCompletionContractTest {

    @Test
    fun successful_restart_publishes_service_recovered_event() {

        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        val registry = RuntimeServiceRegistry()

        val service = TestService()

        registry.register(service)

        registry.startAll()

        registry.restart("service")

        val recovered =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceRecovered>()

        assertEquals(
            1,
            recovered.size
        )

        assertEquals(
            "service",
            recovered.first().serviceName
        )
    }


    @AfterTest
    fun cleanup() {
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
