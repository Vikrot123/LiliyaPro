package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceState

class RuntimeServiceBootstrapRecoveryContractTest {

    private class RecoverableService : RuntimeService {

        override val name = "bootstrap-recovery-service"

        override var state = RuntimeServiceState.CREATED
            private set

        var starts = 0

        override fun start() {
            starts++

            if (starts == 1) {
                throw IllegalStateException("startup failure")
            }

            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }

    @Test
    fun bootstrap_installs_recovery_pipeline() {

        RuntimeEventBus.clear()

        val service = RecoverableService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val bootstrap = RuntimeServiceBootstrap(provider)

        bootstrap.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        assertTrue(
            service.starts >= 2
        )

        assertEquals(
            1,
            bootstrap.getRestartCount(
                "bootstrap-recovery-service"
            )
        )

        RuntimeEventBus.clear()
    }
}
