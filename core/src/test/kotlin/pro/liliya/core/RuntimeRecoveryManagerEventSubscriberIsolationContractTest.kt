package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerEventSubscriberIsolationContractTest {

    @Test
    fun recovery_event_subscribers_are_isolated_between_managers() {
        RuntimeEventBus.clear()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        val busA = RuntimeRecoveryEventBus()
        val busB = RuntimeRecoveryEventBus()

        busA.subscribe {
            eventsA.add(it)
        }

        busB.subscribe {
            eventsB.add(it)
        }

        val registryA = RuntimeServiceRegistry()

        registryA.register(object : RuntimeService {
            override val name = "service-a"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        val registryB = RuntimeServiceRegistry()

        registryB.register(object : RuntimeService {
            override val name = "service-b"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        val supervisorA = RuntimeSupervisor(
            registryProvider = { registryA }
        )

        val supervisorB = RuntimeSupervisor(
            registryProvider = { registryB }
        )

        val managerA = RuntimeRecoveryManager(
            supervisorA,
            registryA,
            busA
        )

        val managerB = RuntimeRecoveryManager(
            supervisorB,
            registryB,
            busB
        )

        managerA.recover("service-a")
        managerB.recover("service-b")

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service-a"),
                RuntimeRecoveryEvent.Completed("service-a")
            ),
            eventsA
        )

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service-b"),
                RuntimeRecoveryEvent.Completed("service-b")
            ),
            eventsB
        )

        RuntimeEventBus.clear()
    }
}
