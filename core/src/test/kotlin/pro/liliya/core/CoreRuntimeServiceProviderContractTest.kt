package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceState

class CoreRuntimeServiceProviderContractTest {

    @Test
    fun runtimeServicesFromProviderFollowCoreRuntimeLifecycle() {

        val service = TestRuntimeService()

        CoreRuntime.setRuntimeServiceProvider(
            object : RuntimeServiceProvider {
                override fun provideServices(): List<RuntimeService> {
                    return listOf(service)
                }
            }
        )

        CoreRuntime.stop()
        CoreRuntime.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        CoreRuntime.stop()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )

        CoreRuntime.resetRuntimeServiceProvider()
    }


    private class TestRuntimeService : RuntimeService {

        override val name = "TEST_PROVIDER_SERVICE"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
