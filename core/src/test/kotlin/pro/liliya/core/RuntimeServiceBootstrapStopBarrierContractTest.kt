package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapStopBarrierContractTest {

    @Test
    fun bootstrap_stop_removes_recovery_event_processing() {
        RuntimeEventBus.clear()

        val service = RecoverableService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            RuntimeServiceRegistry()
        )

        bootstrap.start()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "before stop"
            )
        )

        assertEquals(
            1,
            bootstrap.getRestartCount(service.name)
        )

        bootstrap.stop()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "after stop"
            )
        )

        assertEquals(
            0,
            bootstrap.getRestartCount(service.name)
        )

        RuntimeEventBus.clear()
    }

    private class RecoverableService : RuntimeService {

        override val name = "bootstrap-stop-barrier-service"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
