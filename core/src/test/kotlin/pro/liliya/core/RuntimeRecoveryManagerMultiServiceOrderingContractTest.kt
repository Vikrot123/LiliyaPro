package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerMultiServiceOrderingContractTest {

    @Test
    fun different_services_recover_independently() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "service-a"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registry.register(object : RuntimeService {
            override val name = "service-b"
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

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service-a",
                reason = "failure-a",
                sourceRegistry = registry
            )
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service-b",
                reason = "failure-b",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("service-a")
        )

        assertEquals(
            1,
            supervisor.getRestartCount("service-b")
        )

        RuntimeEventBus.clear()
    }
}
