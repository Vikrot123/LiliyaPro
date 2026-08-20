package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceBootstrapPrepareProviderIsolationContractTest {

    @Test
    fun fresh_bootstrap_does_not_retain_custom_provider_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val staleService = object : RuntimeService {
            override val name = "stale-provider-service"
            override val state = RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        val customProvider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(staleService)
            }
        }

        composition.setRuntimeServiceProvider(customProvider)

        composition.prepareRuntime()

        val freshBootstrap = composition.serviceBootstrap()

        freshBootstrap.start()

        assertEquals(
            emptyMap(),
            freshBootstrap.getStates()
        )

        freshBootstrap.stop()
    }
}
