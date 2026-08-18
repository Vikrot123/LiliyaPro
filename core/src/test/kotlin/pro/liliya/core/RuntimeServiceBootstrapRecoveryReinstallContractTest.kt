package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapRecoveryReinstallContractTest {

    @Test
    fun bootstrap_restart_reinstalls_single_recovery_pipeline() {
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

        bootstrap.stop()

        bootstrap.start()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "restart recovery"
            )
        )

        assertEquals(
            1,
            bootstrap.getRestartCount(service.name)
        )

        assertTrue(
            service.starts >= 2
        )

        bootstrap.stop()

        RuntimeEventBus.clear()
    }

    private class RecoverableService : RuntimeService {

        override val name = "bootstrap-reinstall-recovery-service"

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
