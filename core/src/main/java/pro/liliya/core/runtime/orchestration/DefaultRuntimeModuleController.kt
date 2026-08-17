package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

class DefaultRuntimeModuleController(
    private val composition: RuntimeModuleComposition
) : RuntimeModuleController {

    override fun startRuntimeComponents(): ModuleManager {
        val manager = composition.createModuleRuntime()
        composition.setModuleManager(manager)
        start(manager)
        return manager
    }

    override fun start(
        manager: ModuleManager
    ) {
        composition.startModuleRuntime(manager)
    }

    override fun stopRuntimeModules() {
        composition.moduleManager()?.let {
            composition.setModuleStates(it.getModuleStates())

            try {
                stop(it)
            } catch (_: Exception) {
            }
        }

        clear()
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
