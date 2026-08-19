package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeModuleProviderPrepareIsolationContractTest {

    @Test
    fun module_provider_returns_to_default_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val defaultProvider =
            composition.moduleProviderHolder()
                .get()

        val customProvider = object : ModuleProvider {
            override fun provideModules(): List<LiliyaModule> =
                emptyList()
        }

        composition.moduleProviderHolder()
            .set(customProvider)

        assertSame(
            customProvider,
            composition.moduleProviderHolder().get()
        )

        composition.prepareRuntime()

        assertSame(
            defaultProvider,
            composition.moduleProviderHolder().get()
        )
    }

    @Test
    fun module_provider_holder_state_resets_without_replacing_holder() {
        val composition = DefaultRuntimeComposition()

        val holderBefore =
            composition.moduleProviderHolder()

        holderBefore.set(
            object : ModuleProvider {
                override fun provideModules(): List<LiliyaModule> =
                    emptyList()
            }
        )

        composition.prepareRuntime()

        val holderAfter =
            composition.moduleProviderHolder()

        assertSame(
            holderBefore,
            holderAfter
        )
    }
}
