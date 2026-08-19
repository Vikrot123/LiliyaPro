package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceProviderPrepareIsolationContractTest {

    @Test
    fun runtime_service_provider_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val originalProvider = composition.runtimeServiceProvider()

        val customProvider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> = emptyList()
        }

        composition.setRuntimeServiceProvider(customProvider)

        assertSame(
            customProvider,
            composition.runtimeServiceProvider()
        )

        composition.prepareRuntime()

        assertSame(
            originalProvider,
            composition.runtimeServiceProvider()
        )
    }
}
