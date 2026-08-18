package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState

class RuntimeServiceBootstrapRestartIsolationContractTest {

    private class CountingService(
        override val name: String
    ) : RuntimeService {

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

    @Test
    fun bootstrap_restart_does_not_retain_previous_registered_services() {
        val first = CountingService("first-service")

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(first)
            }
        }

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            RuntimeServiceRegistry()
        )

        bootstrap.start()
        bootstrap.stop()

        bootstrap.start()

        assertEquals(
            2,
            first.starts
        )

        bootstrap.stop()
    }
}
