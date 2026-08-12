package pro.liliya.core

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState

class RollbackFailingModule : LiliyaModule {

    override val descriptor =
        ModuleDescriptor(
            name = "FAILING_MODULE",
            version = "1.0",
            critical = false
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
