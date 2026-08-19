package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleProviderPrepareIsolationContractTest {

    @Test
    fun module_provider_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val originalProvider = composition.moduleProvider()

        val customProvider = object : ModuleProvider {
            override fun provideModules(): List<LiliyaModule> {
                return emptyList()
            }
        }

        composition.setModuleProvider(customProvider)

        assertSame(
            customProvider,
            composition.moduleProvider()
        )

        composition.prepareRuntime()

        assertSame(
            originalProvider,
            composition.moduleProvider()
        )
    }
}
