package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapFailureIsolationContractTest {

    @Test
    fun bootstrap_failure_does_not_leave_partial_lifecycle_state() {
        RuntimeEventBus.clear()

        val healthy = HealthyService()
        val failing = FailingService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(
                    healthy,
                    failing
                )
            }
        }

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            RuntimeServiceRegistry()
        )

        bootstrap.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            healthy.state
        )

        assertTrue(
            bootstrap.getFailures()
                .any { it.serviceName == failing.name }
        )

        bootstrap.stop()

        bootstrap.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            healthy.state
        )

        bootstrap.stop()

        RuntimeEventBus.clear()
    }

    private class HealthyService : RuntimeService {

        override val name = "healthy-bootstrap-service"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }

    private class FailingService : RuntimeService {

        override val name = "failing-bootstrap-service"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            throw IllegalStateException("bootstrap failure")
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
