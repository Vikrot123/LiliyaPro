package pro.liliya.core.runtime.module

import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.runtime.capability.RuntimeCapabilityDiscoveryRegistry
import pro.liliya.core.runtime.capability.RuntimeModuleCapabilityBinder

class DefaultRuntimeModuleCapabilityLifecycle(
    private val binder: RuntimeModuleCapabilityBinder,
    private val registry: RuntimeCapabilityDiscoveryRegistry
) : RuntimeModuleCapabilityLifecycle {

    override fun onModuleInit(
        module: LiliyaModule
    ) {
        val provider =
            registry.discover(module)

        if (provider != null) {
            binder.bind(provider)
        }
    }


    override fun onModuleShutdown(
        module: LiliyaModule
    ) {
        val provider =
            registry.discover(module)

        if (provider != null) {
            binder.unbind(provider)
        }
    }
}
