package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapHolderCleanupContractTest {

    @Test
    fun replace_stopsServicesFromOldBootstrap() {
        val service = TrackingRuntimeService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val registry = RuntimeServiceRegistry()

        val oldBootstrap = RuntimeServiceBootstrap(
            provider,
            registry
        )

        val newBootstrap = RuntimeServiceBootstrap(
            provider,
            registry
        )

        val holder = RuntimeServiceBootstrapHolder {
            RuntimeServiceBootstrap(
                provider,
                registry
            )
        }

        oldBootstrap.start()

        holder.replace(oldBootstrap)
        holder.replace(newBootstrap)

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )
    }

    private class TrackingRuntimeService : RuntimeService {

        override val name = "TRACKING_SERVICE"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
