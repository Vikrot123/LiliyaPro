package pro.liliya.core

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleProvider

class FailingModuleProvider : ModuleProvider {

    override fun provideModules(): List<LiliyaModule> {
        return CriticalFailingModuleProvider()
            .provideModules()
    }
}
