package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeServiceProviderPrepareIsolationContractTest {

    @Test
    fun custom_provider_is_reset_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val defaultProvider =
            composition.runtimeServiceProvider()

        val customProvider = object : RuntimeServiceProvider {
            override fun provideServices() =
                emptyList<pro.liliya.core.runtime.RuntimeService>()
        }

        composition.runtimeServiceProviderHolder()
            .set(customProvider)

        assertSame(
            customProvider,
            composition.runtimeServiceProvider()
        )

        composition.prepareRuntime()

        assertSame(
            defaultProvider,
            composition.runtimeServiceProvider()
        )
    }

    @Test
    fun provider_reset_does_not_keep_custom_instance() {
        val composition = DefaultRuntimeComposition()

        val customProvider = object : RuntimeServiceProvider {
            override fun provideServices() =
                emptyList<pro.liliya.core.runtime.RuntimeService>()
        }

        composition.runtimeServiceProviderHolder()
            .set(customProvider)

        composition.prepareRuntime()

        assertNotSame(
            customProvider,
            composition.runtimeServiceProvider()
        )
    }
}
