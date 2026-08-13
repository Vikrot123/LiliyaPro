package pro.liliya.core.runtime.module

import pro.liliya.core.module.LiliyaModule

import pro.liliya.core.runtime.capability.RuntimeModuleCapabilityBinder

interface RuntimeModuleCapabilityLifecycle {

    fun onModuleInit(
        module: LiliyaModule
    )

    fun onModuleShutdown(
        module: LiliyaModule
    )

}
