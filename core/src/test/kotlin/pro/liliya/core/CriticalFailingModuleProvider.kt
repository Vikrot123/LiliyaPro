package pro.liliya.core

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleProvider
import pro.liliya.core.module.ModuleState

class CriticalFailingModuleProvider : ModuleProvider {

    override fun provideModules(): List<LiliyaModule> {
        return listOf(
            CriticalStartupFailingModule()
        )
    }
}

private class CriticalStartupFailingModule : LiliyaModule {

    override val descriptor = ModuleDescriptor(
        name = "CRITICAL_STARTUP_FAILING_MODULE",
        version = "1.0",
        critical = true
    )

    override var state = ModuleState.CREATED

    override fun init() {
        state = ModuleState.INITIALIZED
    }

    override fun start() {
        throw RuntimeException("Intentional startup failure")
    }

    override fun stop() {
        state = ModuleState.STOPPED
    }
}
