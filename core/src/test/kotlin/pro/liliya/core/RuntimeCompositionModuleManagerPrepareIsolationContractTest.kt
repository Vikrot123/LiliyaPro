package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleManagerPrepareIsolationContractTest {

    @Test
    fun module_manager_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val manager = ModuleManager(
            ModuleRegistry(),
            object : ModuleProvider {
                override fun provideModules(): List<LiliyaModule> = emptyList()
            }
        )

        composition.setModuleManager(manager)

        assertSame(
            manager,
            composition.moduleManager()
        )

        composition.prepareRuntime()

        assertNull(
            composition.moduleManager()
        )
    }
}
