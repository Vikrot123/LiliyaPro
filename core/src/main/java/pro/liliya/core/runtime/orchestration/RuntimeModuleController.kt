package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

interface RuntimeModuleController {

    fun startRuntimeComponents(): ModuleManager

    fun stopRuntimeModules()
}
