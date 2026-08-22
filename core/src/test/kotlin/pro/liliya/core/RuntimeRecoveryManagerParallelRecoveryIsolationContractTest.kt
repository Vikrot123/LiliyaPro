package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerParallelRecoveryIsolationContractTest {

    @Test
    fun parallel_recovery_of_different_services_is_isolated() {

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

        registry.register(
            object : RuntimeService {

                override val name = "service-B"

                override val state =
                    RuntimeServiceState.RUNNING

                override fun start() {
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
            manager.recover("service-B")
        }

        first.join()
        second.join()

        assertEquals(
            2,
            supervisor.getRestartCount("service-A") +
                supervisor.getRestartCount("service-B")
        )

        assertEquals(
            setOf(
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Completed("service-A"),
                RuntimeRecoveryEvent.Started("service-B"),
                RuntimeRecoveryEvent.Completed("service-B")
            ),
            events.toSet()
        )
    }
}
