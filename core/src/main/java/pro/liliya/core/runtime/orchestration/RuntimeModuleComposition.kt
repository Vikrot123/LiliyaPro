package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

interface RuntimeModuleComposition {

    fun startModuleRuntime(
        manager: ModuleManager
    )

    fun stopModuleRuntime(
        manager: ModuleManager
    )

    fun clearModuleRuntime()
}
