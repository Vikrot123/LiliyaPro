package pro.liliya.core

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState

class FailingModule : LiliyaModule {

    override val descriptor = ModuleDescriptor(
        name = "FAILING_MODULE",
        version = "0.3",
        critical = false
    )

    override var state: ModuleState =
        ModuleState.CREATED

    override fun init() {
        throw RuntimeException(
            "Intentional test failure"
        )
    }

    override fun start() {
        state = ModuleState.RUNNING
    }

    override fun stop() {
        state = ModuleState.STOPPED
    }
}
