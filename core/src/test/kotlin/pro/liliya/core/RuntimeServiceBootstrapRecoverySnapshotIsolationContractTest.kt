package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapRecoverySnapshotIsolationContractTest {

    @Test
    fun bootstrap_restart_clears_recovery_snapshot_state() {
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
                reason = "snapshot failure"
            )
        )

        assertEquals(
            1,
            bootstrap.getRestartCount(service.name)
        )

        bootstrap.stop()

        bootstrap.start()

        assertEquals(
            0,
            bootstrap.getRestartCount(service.name)
        )

        bootstrap.stop()

        RuntimeEventBus.clear()
    }

    private class RecoverableService : RuntimeService {
        override val name = "bootstrap-snapshot-isolation-service"

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
