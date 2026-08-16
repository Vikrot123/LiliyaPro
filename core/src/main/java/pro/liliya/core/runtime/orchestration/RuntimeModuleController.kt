package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

interface RuntimeModuleController {

    fun start(
        manager: ModuleManager
    )

    fun stop(
        manager: ModuleManager
    )

    fun clear()
}
