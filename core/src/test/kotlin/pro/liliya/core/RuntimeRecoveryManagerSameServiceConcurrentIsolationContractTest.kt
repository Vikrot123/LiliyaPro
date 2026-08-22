package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerSameServiceConcurrentIsolationContractTest {

    @Test
    fun concurrent_recovery_of_same_service_does_not_corrupt_state() {

        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {

                override val name = "service-A"

                override val state =
                    RuntimeServiceState.RUNNING

                override fun start() {
                    Thread.sleep(50)
                }

                override fun stop() {
                }
            }
        )

        val supervisor = RuntimeSupervisor(
            registryProvider = {
                registry
            }
        )

        val recoveryBus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        recoveryBus.subscribe {
            synchronized(events) {
                events.add(it)
            }
        }

        val manager = RuntimeRecoveryManager(
            supervisor = supervisor,
            recoveryEventBus = recoveryBus
        )

        val first = thread {
            manager.recover("service-A")
        }

        val second = thread {
            manager.recover("service-A")
        }

        first.join()
        second.join()

        assertEquals(
            2,
            supervisor.getRestartCount("service-A")
        )

        assertEquals(
            4,
            events.size
        )

        assertEquals(
            listOf(
                "service-A",
                "service-A",
                "service-A",
                "service-A"
            ),
            events.map {
                when (it) {
                    is RuntimeRecoveryEvent.Started ->
                        it.serviceName

                    is RuntimeRecoveryEvent.Completed ->
                        it.serviceName

                    is RuntimeRecoveryEvent.Failed ->
                        it.serviceName
                }
            }
        )
    }
}
