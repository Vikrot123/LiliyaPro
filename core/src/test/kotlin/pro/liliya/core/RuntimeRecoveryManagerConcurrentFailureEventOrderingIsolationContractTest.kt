package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerConcurrentFailureEventOrderingIsolationContractTest {

    @Test
    fun concurrent_failure_events_keep_recovery_order_isolated() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val recoveryBus = RuntimeRecoveryEventBus()

        recoveryBus.subscribe {
            events.add(it)
        }

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "ordered-concurrent-service"
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
            registry,
            recoveryBus
        )

        manager.install()

        val threads = (1..5).map {
            thread {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = "ordered-concurrent-service",
                        reason = "failure-$it",
                        sourceRegistry = registry
                    )
                )
            }
        }

        threads.forEach {
            it.join()
        }

        assertEquals(
            1,
            supervisor.getRestartCount("ordered-concurrent-service")
        )

        assertEquals(
                emptyList(),
                events
            )

        RuntimeEventBus.clear()
    }
}
