package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapIdempotentLifecycleContractTest {

    @Test
    fun bootstrap_start_and_stop_are_idempotent() {
        RuntimeEventBus.clear()

        val service = CountingService()

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
        bootstrap.start()

        assertEquals(
            1,
            service.starts
        )

        bootstrap.stop()
        bootstrap.stop()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )

        bootstrap.start()

        assertEquals(
            2,
            service.starts
        )

        bootstrap.stop()

        RuntimeEventBus.clear()
    }

    private class CountingService : RuntimeService {

        override val name = "bootstrap-idempotent-service"

        override var state = RuntimeServiceState.CREATED
            private set

        var starts = 0

        override fun start() {
            starts++
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
