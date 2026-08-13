package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState

class RuntimeServiceHealthContractTest {

    private class HealthyService : RuntimeService {

        override val name = "healthy"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }

    private class FailedService : RuntimeService {

        override val name = "failed"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            throw IllegalStateException("boot failed")
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }

    @Test
    fun healthy_service_reports_healthy_state() {

        val registry = RuntimeServiceRegistry()

        registry.register(HealthyService())
        registry.startAll()

        val health = registry.getHealth()["healthy"]

        assertTrue(health != null)
        assertTrue(health!!.healthy)
        assertEquals(RuntimeServiceState.RUNNING, health.state)
    }

    @Test
    fun failed_service_reports_failure() {

        val registry = RuntimeServiceRegistry()

        registry.register(FailedService())
        registry.startAll()

        val health = registry.getHealth()["failed"]

        assertTrue(health != null)
        assertFalse(health!!.healthy)
        assertTrue(health.lastFailure != null)
        assertEquals("boot failed", health.lastFailure!!.reason)
    }
}
