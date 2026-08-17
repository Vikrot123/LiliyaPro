package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

interface RuntimeModuleComposition {

    fun createModuleRuntime(): ModuleManager

    fun setModuleManager(manager: ModuleManager)

    fun moduleManager(): ModuleManager?
    fun setModuleStates(
        states: Map<String, pro.liliya.core.module.ModuleState>
    )


    fun startModuleRuntime(
        manager: ModuleManager
    )

    fun stopModuleRuntime(
        manager: ModuleManager
    )

    fun clearModuleRuntime()
}
