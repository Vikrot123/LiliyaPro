package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerRecoveryAfterFailureIsolationContractTest {

    @Test
    fun recovery_after_failure_starts_clean_second_attempt() {

        val registry = RuntimeServiceRegistry()

        var failFirstStart = true

        registry.register(
            object : RuntimeService {

                override val name = "service-A"

                override val state =
                    RuntimeServiceState.RUNNING

                override fun start() {

                    if (failFirstStart) {
                        failFirstStart = false
                        throw IllegalStateException("first failure")
                    }
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
            events.add(it)
        }

        val manager = RuntimeRecoveryManager(
            supervisor = supervisor,
            recoveryEventBus = recoveryBus
        )

        val firstResult =
            manager.recover("service-A")

        val secondResult =
            manager.recover("service-A")


        assertEquals(
            false,
            firstResult
        )

        assertEquals(
            true,
            secondResult
        )


        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Failed("service-A"),
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Completed("service-A")
            ),
            events
        )


        val snapshot =
            manager.snapshot()


        assertEquals(
            "service-A",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )


        assertEquals(
            2,
            supervisor.getRestartCount("service-A")
        )
    }
}
