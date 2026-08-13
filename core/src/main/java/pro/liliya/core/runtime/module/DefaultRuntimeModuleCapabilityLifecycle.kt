package pro.liliya.core.runtime.module

import pro.liliya.core.module.LiliyaModule

import pro.liliya.core.runtime.capability.RuntimeModuleCapabilityBinder

class DefaultRuntimeModuleCapabilityLifecycle(
    private val binder: RuntimeModuleCapabilityBinder
) : RuntimeModuleCapabilityLifecycle {

    override fun onModuleInit(
        module: LiliyaModule
    ) {
        if (module is CapabilityAwareModule) {
            binder.bind(
                module.capabilityProvider()
            )
        }
    }

    override fun onModuleShutdown(
        module: LiliyaModule
    ) {
        if (module is CapabilityAwareModule) {
            binder.unbind(
                module.capabilityProvider()
            )
        }
    }
}
