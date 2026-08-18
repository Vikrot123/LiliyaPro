package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapHolderLifecycleContractTest {

    @Test
    fun holder_reset_creates_clean_bootstrap_instance() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val supervisor = RuntimeSupervisor(
    registryProvider = {
        registry
    }
)

        val recoveryManager = RuntimeRecoveryManager(supervisor)

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return emptyList()
            }
        }

        val holder = RuntimeServiceBootstrapHolder {
            RuntimeServiceBootstrap(
                provider,
                registry,
                supervisor,
                recoveryManager
            )
        }

        val first = holder.get()

        first.start()
        first.stop()

        holder.reset()

        val second = holder.get()

        assertNotSame(first, second)

        second.start()

        assertEquals(
            0,
            supervisor.getRestartCounts().values.sum()
        )

        second.stop()

        RuntimeEventBus.clear()
    }
}
