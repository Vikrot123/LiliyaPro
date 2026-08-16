package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

class DefaultRuntimeModuleController(
    private val composition: RuntimeModuleComposition
) : RuntimeModuleController {

    override fun start(
        manager: ModuleManager
    ) {
        composition.startModuleRuntime(manager)
    }

    override fun stop(
        manager: ModuleManager
    ) {
        composition.stopModuleRuntime(manager)
    }

    override fun clear() {
        composition.clearModuleRuntime()
    }
}
